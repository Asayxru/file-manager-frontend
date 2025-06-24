import React, { useState, useEffect } from 'react';
import axios from '../api/axios';

const Profile = () => {
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const [token, setToken] = useState(null);

  useEffect(() => {
    const jwt = localStorage.getItem('jwt');
    if (jwt) {
      setToken(jwt);
      axios.defaults.headers.common['Authorization'] = `Bearer ${jwt}`;
    } else {
      setMessage('Ви не авторизовані');
    }
  }, []);

  const handlePasswordChange = async () => {
    try {
      await axios.put('/users/me', { password });
      setMessage('✅ Пароль оновлено!');
      setPassword('');
    } catch (err) {
      setMessage('❌ Помилка при оновленні пароля');
      console.error(err);
    }
  };

  const handleDeleteAccount = async () => {
    if (!window.confirm('Ви впевнені, що хочете видалити акаунт?')) return;
    try {
      await axios.delete('/users/me');
      localStorage.removeItem('jwt');
      setMessage('✅ Акаунт видалено');
      window.location.href = '/login';
    } catch (err) {
      setMessage('❌ Помилка при видаленні акаунту');
      console.error(err);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: 'auto', padding: '1rem' }}>
      <h2>Особистий кабінет</h2>

      <input
        type="password"
        placeholder="Новий пароль"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        style={{ width: '100%', padding: '0.5rem', marginBottom: '1rem' }}
      />
      <button onClick={handlePasswordChange} style={{ width: '100%' }}>
        🔐 Змінити пароль
      </button>

      <hr />

      <button
        onClick={handleDeleteAccount}
        style={{
          width: '100%',
          backgroundColor: '#ffdddd',
          color: 'red',
          padding: '0.5rem',
          marginTop: '1rem',
        }}
      >
        🗑 Видалити акаунт
      </button>

      {message && (
        <p style={{ marginTop: '1rem', textAlign: 'center', color: 'gray' }}>{message}</p>
      )}
    </div>
  );
};

export default Profile;
