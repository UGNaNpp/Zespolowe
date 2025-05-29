import React from 'react';
import api from 'axios';

const SendRequestButton = () => {
    const handleButtonClick = async () => {
        try {
            const response = await api.get("http://localhost:8080/api/security/test-endpoint", {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            console.log('Response data:', response.data);
            alert("Security works")
        } catch (error) {
            // @ts-expect-error this-is-to-only-test
            if (error.response) {
                // @ts-expect-error this-is-to-only-test
                console.error('Error:', error.response.data);
            } else { // @ts-expect-error this-is-to-only-test
                if (error.request) {
                                // @ts-expect-error this-is-to-only-test
                                console.error('No response received:', error.request);
                            } else {
                                // @ts-expect-error this-is-to-only-test
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
