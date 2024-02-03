import React, { useState } from "react";
import UserServeice from "../services/UsersService";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const RegisterComponent = () => {
  const cardStyle = {
    backgroundColor: "rgba(50, 50, 50, 0.7)",
  };

  //  state for form fields
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
    isAdmin: "false",
  });

  const [successMessage, setSuccessMessage] = useState("");

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // FE validation
    if (formData.password !== formData.confirmPassword) {
      return; // passwords don't match
    } else {
      UserServeice.sendRegisterUser(formData)
        .then((response) => {
          //display success message and clear form fields
          console.log(response);
          console.log(formData);
          setSuccessMessage("Registration successful!");
          setFormData({
            username: "",
            email: "",
            password: "",
            confirmPassword: "",
          });
          toast.success(
            "You have been registered successfully, please Log in!"
          );
        })
        .catch((error) => {
          // Handle
          console.error("Registration failed:", error);
          toast.error("Register Failed. Please check your data");
        });
    }
  };

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-sm-10 col-md-10">
          <div className="card text-white" style={cardStyle}>
            <div className="card-header text-light">Register</div>
            <div className="card-body">
              <form onSubmit={handleSubmit}>
                <div className="form-group mt-2">
                  <label>Username</label>
                  <input
                    id="registerUsername"
                    type="text"
                    className="form-control"
                    name="username"
                    value={formData.username}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group mt-2">
                  <label>Email</label>
                  <input
                    id="registerEmail"
                    type="email"
                    className="form-control"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group mt-2">
                  <label>Password</label>
                  <input
                    id="registerPassword"
                    type="password"
                    className="form-control"
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                  />
                </div>
                <div className="form-group mt-2">
                  <label>Confirm Password</label>
                  <input
                    id="registerConfirmPassword"
                    type="password"
                    className="form-control"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleInputChange}
                  />
                </div>
                {formData.password !== formData.confirmPassword && (
                  <div id="passwordMatchText" className="text-danger">
                    {" "}
                    <p>Passwords must match!</p>
                  </div>
                )}
                <button
                  id="registerButton"
                  type="submit"
                  className="btn btn-warning mt-3"
                >
                  Register
                </button>
              </form>
              {successMessage && (
                <div className="mt-3 text-success">{successMessage}</div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterComponent;
