import React, { useState, useEffect } from 'react';
import { getHealthStatus } from './services/api';
import { Activity, Database, CheckCircle2, AlertCircle, RefreshCw } from 'lucide-react';

export default function App() {
  const [healthData, setHealthData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchHealth = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getHealthStatus();
      setHealthData(data);
    } catch (err) {
      setError(err.message || 'Unable to connect to backend server');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHealth();
  }, []);

  const isConnected = healthData?.success && healthData?.data?.database?.status === 'CONNECTED';

  return (
    <div className="container">
      {/* Header Badge */}
      <div className="badge">
        Team Code_Crew • Infosys Springboard Project
      </div>

      {/* Main Title & Purpose */}
      <h1>Development of a Smart Water Usage Monitoring and Automated Billing Management Platform</h1>
      <p className="subtitle">
        An intelligent IoT telemetry, tiered-tariff billing, and community water accounting platform designed to optimize multi-family residential water resources, detect anomalies, and automate fair cost allocation.
      </p>

      {/* Development Status Card */}
      <div className="card">
        <h2 style={{ fontSize: '1.25rem', marginBottom: '16px', color: '#0f172a' }}>
          Current Development Stage: Foundation Milestone
        </h2>
        <p style={{ color: '#475569', fontSize: '0.95rem', lineHeight: '1.6' }}>
          The foundational architecture has been initialized with separated <code>backend/</code> and <code>frontend/</code> modules, Java 17 LTS, Spring Boot 3.5.16, Spring Security 6 stateless filters, environment-driven PostgreSQL configuration, and this React 18/Vite user interface.
        </p>

        <div className="grid-two">
          <div className="info-box">
            <div className="info-title">Backend Architecture</div>
            <div className="info-value">Spring Boot 3.5.16 / Java 17</div>
            <div style={{ fontSize: '0.85rem', color: '#64748b', marginTop: '6px' }}>
              Spring Security 6 • Flyway Migrations • OpenAPI 3.0
            </div>
          </div>

          <div className="info-box">
            <div className="info-title">Frontend Architecture</div>
            <div className="info-value">React 18.3.1 / Vite 5.4</div>
            <div style={{ fontSize: '0.85rem', color: '#64748b', marginTop: '6px' }}>
              Axios Client • Lucide Icons • Recharts Ready
            </div>
          </div>
        </div>
      </div>

      {/* Live System Diagnostics Card */}
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h2 style={{ fontSize: '1.25rem', color: '#0f172a' }}>Live Health Diagnostics</h2>
            <p style={{ color: '#64748b', fontSize: '0.875rem' }}>
              Reporting live status from endpoint: <code>/api/v1/health</code>
            </p>
          </div>
          <button onClick={fetchHealth} disabled={loading} className="refresh-btn">
            <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
            {loading ? 'Checking...' : 'Refresh Status'}
          </button>
        </div>

        {error ? (
          <div style={{ marginTop: '20px', padding: '16px', background: '#fef2f2', border: '1px solid #fecaca', borderRadius: '8px', color: '#991b1b', display: 'flex', alignItems: 'center', gap: '12px' }}>
            <AlertCircle size={24} />
            <div>
              <div style={{ fontWeight: 600 }}>Backend Connection Status: Offline / Unreachable</div>
              <div style={{ fontSize: '0.875rem' }}>Ensure the Spring Boot backend server is running on port 8080. ({error})</div>
            </div>
          </div>
        ) : healthData ? (
          <div style={{ marginTop: '20px' }}>
            <div className="health-status-row">
              <span style={{ display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 500 }}>
                <Activity size={18} color="#0284c7" />
                Backend Application Service
              </span>
              <span className={`badge ${healthData?.data?.status === 'UP' || healthData?.data?.status === 'DEGRADED' ? 'badge-status' : 'badge-degraded'}`} style={{ margin: 0 }}>
                {healthData?.data?.status || 'UNKNOWN'}
              </span>
            </div>

            <div className="health-status-row">
              <span style={{ display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 500 }}>
                <Database size={18} color="#0284c7" />
                PostgreSQL Database Connection
              </span>
              <span className={`badge ${isConnected ? 'badge-status' : 'badge-degraded'}`} style={{ margin: 0 }}>
                {healthData?.data?.database?.status || 'DISCONNECTED'}
              </span>
            </div>

            <div style={{ marginTop: '12px', fontSize: '0.85rem', color: '#64748b' }}>
              {isConnected ? (
                <span style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#166534' }}>
                  <CheckCircle2 size={16} />
                  Connected to: {healthData?.data?.database?.databaseProduct || 'PostgreSQL'}
                </span>
              ) : (
                <span>
                  Notice: {healthData?.data?.database?.details || 'Database service disconnected. Verify PostgreSQL service on port 5432.'}
                </span>
              )}
            </div>
          </div>
        ) : null}
      </div>

      <div className="footer">
        Code_Crew Platform • Infosys Springboard Project 2026
      </div>
    </div>
  );
}
