import React from 'react';
import Login from '../components/loginComponent'; 
import Register from '../components/registerComponent'; 

const LoginRegisterPage = () => {
    return (
        <div className="container">
            <div className="row">
                <div className="col-md-6 border-end pe-4"> 
                    <Login />
                </div>
                <div className="col-md-6 ps-4">
                    <div className="d-flex flex-column justify-content-center align-items-center">
                        <div className="border rounded-circle p-2 bg-white">OR</div> 
                    </div>
                    <Register />
                </div>
            </div>
        </div>
    );
};

export default LoginRegisterPage;
