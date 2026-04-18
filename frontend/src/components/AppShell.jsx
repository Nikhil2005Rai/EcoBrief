import { useEffect, useMemo, useRef, useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getMeetings } from '../api/meetingApi';
import { useTheme } from '../context/ThemeContext';

function AppShell({ children }) {
  const { email, logout } = useAuth();
  const navigate = useNavigate();
  const { theme, isDark, toggleTheme } = useTheme();
  const searchInputRef = useRef(null);
  const [searchOpen, setSearchOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [meetings, setMeetings] = useState([]);
  const [searchError, setSearchError] = useState('');
  const [searchLoading, setSearchLoading] = useState(true);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  useEffect(() => {
    const loadMeetings = async () => {
      try {
        const response = await getMeetings();
        setMeetings(response);
      } catch (requestError) {
        setSearchError(requestError.response?.data?.message || 'Search is temporarily unavailable');
      } finally {
        setSearchLoading(false);
      }
    };

    loadMeetings();
  }, []);

  useEffect(() => {
    const handleKeyDown = (event) => {
      const isShortcut = (event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k';
      if (isShortcut) {
        event.preventDefault();
        setSearchOpen(true);
      }

      if (event.key === 'Escape') {
        setSearchOpen(false);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  useEffect(() => {
    if (searchOpen) {
      window.setTimeout(() => searchInputRef.current?.focus(), 0);
    } else {
      setSearchQuery('');
      setSearchError('');
    }
  }, [searchOpen]);

  const searchResults = useMemo(() => {
    const query = searchQuery.trim().toLowerCase();
    const orderedMeetings = [...meetings].sort(
      (left, right) => new Date(right.createdAt) - new Date(left.createdAt)
    );

    if (!query) {
      return orderedMeetings.slice(0, 6);
    }

    return orderedMeetings.filter((meeting) => {
      const haystacks = [
        meeting.title,
        meeting.summary,
        meeting.transcript,
        meeting.createdAt,
        ...(meeting.keyPoints || []),
        ...(meeting.actionItems || [])
      ];

      return haystacks.some((value) => value && String(value).toLowerCase().includes(query));
    }).slice(0, 8);
  }, [meetings, searchQuery]);

  const openSearch = () => setSearchOpen(true);
  const closeSearch = () => setSearchOpen(false);

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-top">
          <Link to="/" className="brand">
            <span className="brand-mark">EB</span>
            <div>
              <strong>EchoBrief</strong>
              <p>Your meeting second brain</p>
            </div>
          </Link>

          <button type="button" className="ghost-button theme-toggle theme-toggle-sidebar" onClick={toggleTheme}>
            {isDark ? 'Light theme' : 'Dark theme'}
          </button>
        </div>

        <div className="sidebar-banner">
          <span className="eyebrow">Workspace</span>
          <h2>Capture every meeting, then recall it instantly.</h2>
          <p>Upload recordings, review AI summaries, and keep decisions, tasks, and key moments in one calm place.</p>
        </div>

        <div className="sidebar-section">
          <span className="sidebar-kicker">Navigate</span>
          <nav className="sidebar-nav">
            <NavLink to="/" end>Home</NavLink>
            <NavLink to="/upload">Capture</NavLink>
          </nav>
        </div>

        <div className="sidebar-section sidebar-meta">
          <span className="sidebar-kicker">Account</span>
          <div className="account-card">
            <div className="account-avatar">{email?.[0]?.toUpperCase() || 'E'}</div>
            <div>
              <strong>{email}</strong>
              <p>{theme === 'dark' ? 'Dark theme active' : 'Light theme active'}</p>
            </div>
          </div>
          <button type="button" className="ghost-button logout-button" onClick={handleLogout}>
            Sign out
          </button>
        </div>
      </aside>

      <main className="page-content">
        <header className="topbar">
          <button type="button" className="topbar-search" onClick={openSearch}>
            <span className="search-icon">⌘K</span>
            <span className="topbar-search-placeholder">Search meetings, summaries, or action items</span>
          </button>
          <div className="topbar-status">
            <span className="status-dot" />
            Synced
          </div>
        </header>
        {children}
      </main>

      {searchOpen ? (
        <div className="search-overlay" role="presentation" onMouseDown={closeSearch}>
          <section
            className="search-modal"
            role="dialog"
            aria-modal="true"
            aria-label="Search meetings"
            onMouseDown={(event) => event.stopPropagation()}
          >
            <div className="search-modal-header">
              <div>
                <span className="sidebar-kicker">Search</span>
                <h2>Find a meeting, note, or action item</h2>
              </div>
              <button type="button" className="ghost-button" onClick={closeSearch}>
                Close
              </button>
            </div>

            <label className="search-input-wrap">
              <span className="sr-only">Search meetings</span>
              <input
                ref={searchInputRef}
                type="search"
                value={searchQuery}
                onChange={(event) => setSearchQuery(event.target.value)}
                placeholder="Search titles, summaries, key points, action items..."
              />
            </label>

            <div className="search-hints">
              <span>Cmd/Ctrl+K to open</span>
              <span>Esc to close</span>
              <span>{searchLoading ? 'Loading meetings...' : `${searchResults.length} results`}</span>
            </div>

            {searchError ? <p className="search-error">{searchError}</p> : null}

            <div className="search-results">
              {!searchLoading && !searchError && searchResults.length === 0 ? (
                <div className="search-empty">
                  <h3>No matches found</h3>
                  <p>Try a different title, keyword, or action item.</p>
                </div>
              ) : null}

              {searchResults.map((meeting) => (
                <Link
                  key={meeting.id}
                  to={`/meeting/${meeting.id}`}
                  className="search-result"
                  onClick={closeSearch}
                >
                  <strong>{meeting.title}</strong>
                  <p>{meeting.summary || 'Open the meeting to view the transcript and summary.'}</p>
                  <div className="search-result-meta">
                    <span>{meeting.keyPoints?.length || 0} key points</span>
                    <span>{meeting.actionItems?.length || 0} action items</span>
                  </div>
                </Link>
              ))}
            </div>
          </section>
        </div>
      ) : null}
    </div>
  );
}

export default AppShell;
