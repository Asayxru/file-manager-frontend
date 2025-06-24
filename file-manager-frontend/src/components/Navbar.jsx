
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem('jwt');
    window.location.href = "/login";
  };

  return (
    <div className="navbar">
      <div className="menu-toggle" onClick={() => setIsOpen(!isOpen)}>
        ☰
      </div>
      {isOpen && (
        <div className="dropdown-menu">
          <Link to="/">📁 Dashboard</Link>
          <Link to="/profile">👤 Профіль</Link>
          <button onClick={handleLogout}>🚪 Вийти</button>
        </div>
      )}
    </div>
  );
};

export default Navbar;
