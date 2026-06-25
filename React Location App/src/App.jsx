import React, { useState } from 'react';

export default function App() {
    const [status, setStatus] = useState("Ready to track");
    const [studentName, setStudentName] = useState("");

    const sendLocation = () => {
        if (!studentName.trim()) {
            alert("Please enter your name first!");
            return;
        }

        if (!navigator.geolocation) {
            setStatus("Geolocation not supported by this browser.");
            return;
        }

        setStatus("Getting location...");

        navigator.geolocation.getCurrentPosition(
            (position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                setStatus(`Coordinates found: ${lat}, ${lng}. Sending to Java...`);

               
                const payload = `${lat},${lng},${studentName}`;

                
                fetch('http://10.95.238.253:8080/locate', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'text/plain',
                    },
                    body: payload
                })
                    .then(response => response.text())
                    .then(data => {
                        setStatus(`Success: Connected to Java Server!`);
                    })
                    .catch(err => {
                        setStatus(`Connection failed: Check if Java app is running and IP is correct.`);
                        console.error(err);
                    });
            },
            (error) => {
                setStatus(`Error getting GPS: ${error.message}`);
            }
        );
    };

    return (
        <div style={{ textAlign: 'center', padding: '50px', fontFamily: 'sans-serif' }}>
            <h1>Classroom Direct Location Portal</h1>
            <p>Enter your name and broadcast your live coordinate strings straight to the server.</p>

            <div style={{ margin: '20px' }}>
                <input
                    type="text"
                    placeholder="Enter Your Name"
                    value={studentName}
                    onChange={(e) => setStudentName(e.target.value)}
                    style={{ padding: '10px', fontSize: '16px', width: '250px' }}
                />
            </div>

            <button onClick={sendLocation} style={{ padding: '15px 30px', fontSize: '16px', cursor: 'pointer' }}>
                Broadcast My Location
            </button>

            <p style={{ marginTop: '30px', fontWeight: 'bold', color: '#555' }}>
                Status: <span style={{ color: '#007bff' }}>{status}</span>
            </p>
        </div>
    );
}