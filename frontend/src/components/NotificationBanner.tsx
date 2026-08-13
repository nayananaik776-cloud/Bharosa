import React from 'react';
import { MessageSquare, Bell } from 'lucide-react';

interface NotificationBannerProps {
  message: string;
}

export const NotificationBanner: React.FC<NotificationBannerProps> = ({ message }) => {
  if (!message) return null;

  return (
    <div style={{
      background: 'rgba(30, 41, 59, 0.95)',
      border: '1px solid rgba(99, 102, 241, 0.3)',
      borderRadius: '12px',
      padding: '0.65rem 0.85rem',
      marginBottom: '1rem',
      boxShadow: '0 4px 12px rgba(0, 0, 0, 0.3)',
      display: 'flex',
      alignItems: 'flex-start',
      gap: '0.65rem'
    }}>
      <div style={{
        background: 'rgba(99, 102, 241, 0.2)',
        padding: '0.4rem',
        borderRadius: '8px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: '0.1rem'
      }}>
        <MessageSquare size={16} color="#818cf8" />
      </div>
      <div style={{ flex: 1 }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.15rem' }}>
          <span style={{ fontSize: '0.7rem', fontWeight: 700, color: '#818cf8', textTransform: 'uppercase' }}>
            Bharosa SMS Guardian
          </span>
          <span style={{ fontSize: '0.65rem', color: '#64748b' }}>Just now</span>
        </div>
        <p style={{ fontSize: '0.78rem', color: '#e2e8f0', margin: 0, lineHeight: 1.35, fontStyle: 'italic' }}>
          "{message}"
        </p>
      </div>
    </div>
  );
};
