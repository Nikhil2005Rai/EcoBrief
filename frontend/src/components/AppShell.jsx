import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function AppShell({ children }) {
  const { email, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <Link to="/" className="brand">
          <span className="brand-mark">AM</span>
          <div>
            <strong>AI Meeting Hub</strong>
            <p>Transcribe. Summarize. Act.</p>
          </div>
        </Link>

        <div className="sidebar-banner">
          <span className="eyebrow">Workspace</span>
          <h2>Turn every recording into a shareable briefing.</h2>
          <p>Upload meetings, review action items, and keep your team aligned without replaying the audio.</p>
        </div>

        <nav className="sidebar-nav">
          <NavLink to="/" end>Dashboard</NavLink>
          <NavLink to="/upload">Upload Meeting</NavLink>
        </nav>

        <div className="sidebar-footer">
          <p>{email}</p>
          <button type="button" className="ghost-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </aside>

      <main className="page-content">{children}</main>
    </div>
  );
}

export default AppShell;
