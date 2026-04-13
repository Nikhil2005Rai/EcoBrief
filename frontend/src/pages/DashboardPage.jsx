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
          <h1>Recent meeting intelligence</h1>
          <p>Review meeting summaries, action items, and transcript status from one place.</p>
        </div>
        <Link to="/upload" className="primary-button">Upload meeting</Link>
      </div>

      <div className="stats-grid">
        <article className="stat-card">
          <span className="stat-label">Meetings</span>
          <strong>{meetings.length}</strong>
          <p>Total recordings processed in your workspace.</p>
        </article>
        <article className="stat-card accent">
          <span className="stat-label">Action Items</span>
          <strong>{actionItemCount}</strong>
          <p>Tasks extracted from AI summaries and next steps.</p>
        </article>
        <article className="stat-card">
          <span className="stat-label">Latest Activity</span>
          <strong>{meetings[0] ? new Date(meetings[0].createdAt).toLocaleDateString() : 'No uploads yet'}</strong>
          <p>Most recent meeting processed by the backend.</p>
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
            <div className="meeting-card-top">
              <span className="status-pill">Processed</span>
              <span>{new Date(meeting.createdAt).toLocaleString()}</span>
            </div>
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
