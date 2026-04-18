import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMeetings } from '../api/meetingApi';

function DashboardPage() {
  const [meetings, setMeetings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const actionItemCount = meetings.reduce((total, meeting) => total + (meeting.actionItems?.length || 0), 0);

  useEffect(() => {
    const loadMeetings = async () => {
      try {
        const response = await getMeetings();
        setMeetings(response);
      } catch (requestError) {
        setError(requestError.response?.data?.message || 'Failed to fetch meetings');
      } finally {
        setLoading(false);
      }
    };

    loadMeetings();
  }, []);

  return (
    <section className="page-section">
      <div className="page-header">
        <div>
          <span className="eyebrow">Dashboard</span>
          <h1>Home</h1>
          <p>Scan the latest meeting notes, highlights, and follow-ups without digging through recordings.</p>
        </div>
        <Link to="/upload" className="primary-button">New capture</Link>
      </div>

      <div className="hero-panel">
        <div>
          <span className="hero-label">Notebook view</span>
          <h2>Everything your team said, refined into a simple workspace.</h2>
          <p>
            EchoBrief keeps transcripts, summaries, and action items organized like a
            well-kept notebook.
          </p>
        </div>
        <div className="hero-metrics">
          <div>
            <strong>{meetings.length}</strong>
            <span>Notes saved</span>
          </div>
          <div>
            <strong>{actionItemCount}</strong>
            <span>Action items</span>
          </div>
        </div>
      </div>

      <div className="stats-grid">
        <article className="stat-card">
          <span className="stat-label">Meetings</span>
          <strong>{meetings.length}</strong>
          <p>Total recordings processed in EchoBrief.</p>
        </article>
        <article className="stat-card accent">
          <span className="stat-label">Action Items</span>
          <strong>{actionItemCount}</strong>
          <p>Tasks extracted from AI summaries and next steps.</p>
        </article>
      </div>

      {loading ? <div className="card">Loading meetings...</div> : null}
      {error ? <div className="card error-card">{error}</div> : null}

      {!loading && !error && meetings.length === 0 ? (
        <div className="card empty-state">
          <h2>No meetings yet</h2>
          <p>Upload your first audio file to generate transcripts and summaries.</p>
        </div>
      ) : null}

      <div className="meeting-grid">
        {meetings.map((meeting) => (
          <Link key={meeting.id} to={`/meeting/${meeting.id}`} className="meeting-card">
            <h2>{meeting.title}</h2>
            <p>{meeting.summary || 'Summary will appear here after processing.'}</p>
            <div className="chip-row">
              {meeting.actionItems?.slice(0, 3).map((item) => (
                <span className="chip" key={item}>{item}</span>
              ))}
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
}

export default DashboardPage;
