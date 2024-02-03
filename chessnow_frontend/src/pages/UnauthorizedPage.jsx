import React from 'react';
import 'font-awesome/css/font-awesome.min.css';

const UnauthorizedPage = () => {
  return (
    <div className="container" >
      <div className="row">
        <div className="col col-md-12 text-danger">

          <div className="row h-5">
            <p>
                You do not have permission to view this page. Please <u>Log in</u> or <u> create an account</u> to continue
            </p>
          </div>
          
        </div>
      </div>
    </div>
  );
};

export default UnauthorizedPage;

