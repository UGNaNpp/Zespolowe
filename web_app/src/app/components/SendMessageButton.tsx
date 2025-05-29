import React from 'react';
import axios from 'axios';

const SendRequestButton = () => {
    const handleButtonClick = async () => {
        try {
            const response = await axios.get("http://localhost:8080/api/security/delete-token", {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            console.log('Response data:', response.data);
            alert("Security works")
        } catch (error) {
            // @ts-ignore
            if (error.response) {
                // @ts-ignore
                console.error('Error:', error.response.data);
            } else { // @ts-ignore
                if (error.request) {
                                // @ts-ignore
                                console.error('No response received:', error.request);
                            } else {
                                // @ts-ignore
                    console.error('Request error:', error.message);
                            }
            }
        }
    };

    return (
        <button onClick={handleButtonClick}>
            Wyślij zapytanie na backend
        </button>
    );
};

export default SendRequestButton;
