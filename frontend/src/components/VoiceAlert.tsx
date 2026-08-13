import React, { useState, useEffect } from 'react';
import { Volume2, VolumeX } from 'lucide-react';

interface VoiceAlertProps {
  warningText?: string;
  autoPlay?: boolean;
}

export const VoiceAlert: React.FC<VoiceAlertProps> = ({ warningText, autoPlay = true }) => {
  const [isPlaying, setIsPlaying] = useState(false);
  const [speechSupported, setSpeechSupported] = useState(true);

  useEffect(() => {
    if (!('speechSynthesis' in window)) {
      setSpeechSupported(false);
      return;
    }

    if (warningText && autoPlay) {
      speak(warningText);
    }

    return () => {
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
      }
    };
  }, [warningText, autoPlay]);

  const speak = (text: string) => {
    if (!('speechSynthesis' in window)) return;

    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.rate = 0.95;
    utterance.pitch = 1.0;

    utterance.onstart = () => setIsPlaying(true);
    utterance.onend = () => setIsPlaying(false);
    utterance.onerror = () => setIsPlaying(false);

    window.speechSynthesis.speak(utterance);
  };

  const handleToggle = () => {
    if (isPlaying) {
      window.speechSynthesis.cancel();
      setIsPlaying(false);
    } else if (warningText) {
      speak(warningText);
    }
  };

  if (!speechSupported || !warningText) return null;

  return (
    <button
      onClick={handleToggle}
      className="btn btn-outline"
      style={{
        padding: '0.4rem 0.75rem',
        fontSize: '0.75rem',
        borderRadius: '9999px',
        borderColor: isPlaying ? '#818cf8' : 'rgba(255,255,255,0.15)',
        background: isPlaying ? 'rgba(99, 102, 241, 0.2)' : 'transparent',
        color: isPlaying ? '#818cf8' : '#9ca3af',
        display: 'inline-flex',
        alignItems: 'center',
        gap: '0.35rem'
      }}
    >
      {isPlaying ? <Volume2 size={14} className="pulse-alert" color="#818cf8" /> : <VolumeX size={14} />}
      <span>{isPlaying ? 'Voice Alert Playing...' : 'Play Voice Warning'}</span>
    </button>
  );
};
