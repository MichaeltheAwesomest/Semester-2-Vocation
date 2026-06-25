import React, { useState, useEffect } from 'react';

export default function App() {
  const [status, setStatus] = useState("Ready to track");
  // Hardcoded test profile for Person 1's branch foundation
  const [studentProfile, setStudentProfile] = useState({ name: "Student_A", matric: "RUN/2026/001" });

  useEffect(() => {
    // Set up an automated sequence loop running every 5000ms (5 seconds)
    const trackingInterval = setInterval(() => {
      if (!navigator.geolocation) {
        setStatus("Geolocation not supported");
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude;
          const lng = position.coords.longitude;
          setStatus(`Tracking active. Last ping: ${new Date().toLocaleTimeString()}`);

          // Bundle parameters to send sequentially to your Java IP address
          const payload = `${lat},${lng},${studentProfile.name},${studentProfile.matric}`;

          fetch('http://10.95.238.190:8080/locate', {
            method: 'POST',
            headers: { 'Content-Type': 'text/plain' },
            body: payload
          })
            .catch(err => console.error("Server connection lost. Is Ayo's Java app open?", err));
        },
        (error) => setStatus(`GPS Error: ${error.message}`),
        { enableHighAccuracy: true }
      );

    }, 5000);

    // Clean up the loop if the component unmounts
    return () => clearInterval(trackingInterval);
  }, [studentProfile]);

  return (
    <div style={{ textAlign: 'center', padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>Tracking Stream Engine (Active)</h2>
      <p style={{ color: '#007bff', fontWeight: 'bold' }}>{status}</p>
      <p>System broadcasting background packets to Java server every 5 seconds.</p>
    </div>
  );
}