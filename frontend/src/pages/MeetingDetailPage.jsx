import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getMeetingById } from '../api/meetingApi';

function MeetingDetailPage() {
  const { id } = useParams();
  const [meeting, setMeeting] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadMeeting = async () => {
      try {
        const response = await getMeetingById(id);
        setMeeting(response);
      } catch (requestError) {
        setError(requestError.response?.data?.message || 'Failed to load meeting');
      } finally {
        setLoading(false);
      }
    };

    loadMeeting();
  }, [id]);

  if (loading) {
    return <section className="page-section"><div className="card">Loading meeting...</div></section>;
  }

  if (error) {
    return <section className="page-section"><div className="card error-card">{error}</div></section>;
  }

  return (
    <section className="page-section">
      <div className="page-header">
        <div>
          <h1>{meeting.title}</h1>
          <p>Capture details and AI notes for this meeting.</p>
        </div>
      </div>

      <div className="stats-grid detail-stats">
        <article className="stat-card">
          <span className="stat-label">Key Points</span>
          <strong>{meeting.keyPoints?.length || 0}</strong>
          <p>Highlights extracted from the meeting discussion.</p>
        </article>
        <article className="stat-card accent">
          <span className="stat-label">Action Items</span>
          <strong>{meeting.actionItems?.length || 0}</strong>
          <p>Follow-ups captured for the team.</p>
        </article>
        <article className="stat-card">
          <span className="stat-label">Transcript Size</span>
          <strong>{meeting.transcript?.split(/\s+/).filter(Boolean).length || 0} words</strong>
          <p>Approximate transcript length processed by the backend.</p>
        </article>
      </div>

      <div className="detail-grid">
        <article className="card detail-card">
          <h2>Summary</h2>
          <p>{meeting.summary}</p>
        </article>

        <article className="card detail-card">
          <h2>Key Points</h2>
          <ul className="detail-list">
            {meeting.keyPoints?.map((item) => <li key={item}>{item}</li>)}
          </ul>
        </article>

        <article className="card detail-card accent-card">
          <h2>Action Items</h2>
          <ul className="detail-list">
            {meeting.actionItems?.length ? meeting.actionItems.map((item) => <li key={item}>{item}</li>) : <li>No action items detected.</li>}
          </ul>
        </article>

        <article className="card detail-card transcript-card">
          <h2>Transcript</h2>
          <pre>{meeting.transcript}</pre>
        </article>
      </div>
    </section>
  );
}

export default MeetingDetailPage;
