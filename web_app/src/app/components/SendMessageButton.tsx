import React from 'react';
import axios from 'axios';

const SendRequestButton = () => {
    const handleButtonClick = async () => {
        try {
            const response = await axios.get("http://localhost:8080/active-cameras", {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            console.log('Response data:', JSON.stringify(response.data), response.status);
            alert("Response" + JSON.stringify(response.data))
        } catch (error) {
            // @ts-ignore
            if (error.response) {
                if (error.response.status === 418) {
                    alert("Server is a teapot")
                } else {
                console.error('Error:', error.response.data);
                }
                // @ts-ignore
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
