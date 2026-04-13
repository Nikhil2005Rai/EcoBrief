import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, signup } from '../api/authApi';
import { useAuth } from '../context/AuthContext';

function LoginPage() {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login: saveAuth } = useAuth();

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const action = mode === 'login' ? login : signup;
      const response = await action(form);
      saveAuth(response);
      navigate('/');
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Unable to authenticate');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-copy">
          <span className="eyebrow">Production-ready meeting intelligence</span>
          <h1>Turn raw audio into crisp transcripts, key points, and action items.</h1>
          <p>
            Upload meetings, run speech-to-text, and generate structured summaries
            that your team can actually use.
          </p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="tab-row">
            <button type="button" className={mode === 'login' ? 'tab active' : 'tab'} onClick={() => setMode('login')}>
              Login
            </button>
            <button type="button" className={mode === 'signup' ? 'tab active' : 'tab'} onClick={() => setMode('signup')}>
              Signup
            </button>
          </div>

          <label>
            Email
            <input type="email" name="email" value={form.email} onChange={handleChange} required />
          </label>

          <label>
            Password
            <input type="password" name="password" value={form.password} onChange={handleChange} required minLength={8} />
          </label>

          {error ? <p className="form-error">{error}</p> : null}

          <button type="submit" className="primary-button" disabled={loading}>
            {loading ? 'Please wait...' : mode === 'login' ? 'Login' : 'Create account'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default LoginPage;
