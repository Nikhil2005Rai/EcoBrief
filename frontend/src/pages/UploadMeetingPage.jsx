import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { uploadMeeting } from '../api/meetingApi';

function UploadMeetingPage() {
  const [title, setTitle] = useState('');
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!file) {
      setError('Please choose a supported audio file');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const meeting = await uploadMeeting({ title, file });
      navigate(`/meeting/${meeting.id}`);
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Upload failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="page-section">
      <div className="page-header">
        <div>
          <span className="eyebrow">Upload</span>
          <h1>Add a new meeting recording</h1>
          <p>Supported formats are mp3, wav, flac, aac, ogg, webm, m4a, and mp4. The backend validates file size and format.</p>
        </div>
      </div>

      <form className="card upload-form" onSubmit={handleSubmit}>
        <label>
          Meeting Title
          <input
            type="text"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="Quarterly planning sync"
          />
        </label>

        <label className="file-field">
          Audio File
          <input
            className="sr-only"
            type="file"
            accept=".mp3,.wav,.flac,.aac,.ogg,.oga,.webm,.m4a,.mp4,audio/*"
            onChange={(event) => setFile(event.target.files?.[0] || null)}
          />
          <span className="file-dropzone">
            <span className="file-dropzone-icon">Wave</span>
            <strong>{file ? file.name : 'Choose or drop an audio file'}</strong>
            <small>
              {file
                ? `${(file.size / (1024 * 1024)).toFixed(2)} MB selected`
                : 'Large meetings are uploaded and processed securely on the backend.'}
            </small>
          </span>
        </label>

        {error ? <p className="form-error">{error}</p> : null}

        <button type="submit" className="primary-button" disabled={loading}>
          {loading ? 'Uploading and processing...' : 'Upload & process'}
        </button>
      </form>
    </section>
  );
}

export default UploadMeetingPage;
