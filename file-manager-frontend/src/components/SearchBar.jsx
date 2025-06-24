import React, { useState } from 'react';
import axios from '../api/axios';

const SearchBar = ({ setSearchResults, setPreviewContent }) => {
  const [query, setQuery] = useState('');

  const handleSearch = async () => {
    try {
      const response = await axios.get(`/files`);
      const filtered = response.data.filter(file =>
        file.name.toLowerCase().includes(query.toLowerCase())
      );
      setSearchResults(filtered);
      setPreviewContent(''); // очистити попередній перегляд
    } catch (err) {
      console.error('Помилка пошуку:', err);
    }
  };

  const handleClear = () => {
    setQuery('');
    setSearchResults([]);
    setPreviewContent('');
  };

  return (
    <div style={{ marginTop: '1rem' }}>
      <input
        type="text"
        placeholder="Пошук за іменем файлу..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
      />
      <button onClick={handleSearch}>Пошук</button>
      <button onClick={handleClear} style={{ marginLeft: '0.5rem' }}>Очистити</button>
    </div>
  );
};

export default SearchBar;
