import { Navigate, Route, Routes } from 'react-router-dom';
import AppShell from '../components/AppShell';
import ProtectedRoute from '../components/ProtectedRoute';
import DashboardPage from '../pages/DashboardPage';
import LoginPage from '../pages/LoginPage';
import MeetingDetailPage from '../pages/MeetingDetailPage';
import UploadMeetingPage from '../pages/UploadMeetingPage';

function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={(
          <ProtectedRoute>
            <AppShell>
              <DashboardPage />
            </AppShell>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/upload"
        element={(
          <ProtectedRoute>
            <AppShell>
              <UploadMeetingPage />
            </AppShell>
          </ProtectedRoute>
        )}
      />
      <Route
        path="/meeting/:id"
        element={(
          <ProtectedRoute>
            <AppShell>
              <MeetingDetailPage />
            </AppShell>
          </ProtectedRoute>
        )}
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRouter;
