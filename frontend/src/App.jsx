import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import { ToastProvider } from './components/ToastContext';
import Dashboard from './pages/Dashboard';
import BirdsPage from './pages/BirdsPage';
import BirdDetailPage from './pages/BirdDetailPage';
import LocationsPage from './pages/LocationsPage';
import LocationDetailPage from './pages/LocationDetailPage';
import SightingsPage from './pages/SightingsPage';
import SightingFormPage from './pages/SightingFormPage';
import NotFoundPage from './pages/NotFoundPage';

export default function App() {
  return (
    <ToastProvider>
      <div className="app-shell">
        <Navbar />
        <main className="app-main">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/birds" element={<BirdsPage />} />
            <Route path="/birds/:id" element={<BirdDetailPage />} />
            <Route path="/locations" element={<LocationsPage />} />
            <Route path="/locations/:id" element={<LocationDetailPage />} />
            <Route path="/sightings" element={<SightingsPage />} />
            <Route path="/sightings/new" element={<SightingFormPage mode="create" />} />
            <Route path="/sightings/:id/edit" element={<SightingFormPage mode="edit" />} />
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </ToastProvider>
  );
}
