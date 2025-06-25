import React, { useState, useEffect } from 'react';
import axios from "../api/axios";

const Login = ({ onLogin }) => {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [usernameExists, setUsernameExists] = useState(false);
  const [emailExists, setEmailExists] = useState(false);

  // Перевірка унікальності username
  useEffect(() => {
    const checkUsername = async () => {
      if (username.trim() === '') return;
      try {
        const res = await axios.get(`/users/check-username?username=${username}`);
        setUsernameExists(res.data.exists);
      } catch {
        setUsernameExists(false);
      }
    };
    if (!isLogin) checkUsername();
  }, [username, isLogin]);

  
  useEffect(() => {
    const checkEmail = async () => {
      if (email.trim() === '') return;
      try {
        const res = await axios.get(`/users/check-email?email=${email}`);
        setEmailExists(res.data.exists);
      } catch {
        setEmailExists(false);
      }
    };
    if (!isLogin) checkEmail();
  }, [email, isLogin]);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/auth/login', { username, password });
      const token = response.data.token;
      localStorage.setItem('jwt', token);
      setError('');
      onLogin?.();
    } catch (err) {
      setError('Невірний логін або пароль');
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    if (usernameExists || emailExists) {
      setError('Ім’я користувача або email вже зайняті');
      return;
    }
    try {
      await axios.post('/auth/register', { username, email, password });
      setSuccess('Успішна реєстрація. Тепер увійдіть.');
      setError('');
      setIsLogin(true);
    } catch {
      setError('Помилка реєстрації. Спробуйте пізніше.');
    }
  };

  return (
    <div className="login-container">
      <h2>{isLogin ? 'Увійти' : 'Реєстрація'}</h2>
      <form onSubmit={isLogin ? handleLogin : handleRegister}>
        <input
          type="text"
          placeholder="Ім'я користувача"
          value={username}
          onChange={e => setUsername(e.target.value)}
          required
        />
        {!isLogin && username && usernameExists && (
          <p style={{ color: 'red' }}>Ім’я користувача вже зайняте</p>
        )}

        {!isLogin && (
          <>
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
            />
            {email && emailExists && (
              <p style={{ color: 'red' }}>Email вже використовується</p>
            )}
          </>
        )}

        <input
          type="password"
          placeholder="Пароль"
          value={password}
          onChange={e => setPassword(e.target.value)}
          required
        />

        <button type="submit" disabled={usernameExists || emailExists}>
          {isLogin ? 'Увійти' : 'Зареєструватися'}
        </button>
      </form>

      <p style={{ marginTop: '10px' }}>
        {isLogin ? (
          <>
            Немає акаунта? <button onClick={() => setIsLogin(false)}>Зареєструватися</button>
          </>
        ) : (
          <>
            Вже є акаунт? <button onClick={() => setIsLogin(true)}>Увійти</button>
          </>
        )}
      </p>

      {error && <p style={{ color: 'red' }}>{error}</p>}
      {success && <p style={{ color: 'green' }}>{success}</p>}
    </div>
  );
};

export default Login;
