import React, { useState } from 'react';
import FolderList from '../components/FolderList';
import FileList from '../components/FileList';
import UploadFileForm from '../components/UploadFileForm';
import SearchBar from '../components/SearchBar';
import FilePreview from '../components/FilePreview';

const Dashboard = () => {
  const [searchResults, setSearchResults] = useState([]);
  const [previewContent, setPreviewContent] = useState('');

  const handlePreview = async (fileId) => {
    try {
      const res = await fetch(`http://localhost:8080/api/files/preview/by-id/${fileId}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('jwt')}`
        }
      });
      const text = await res.text();
      setPreviewContent(text);
    } catch (err) {
      setPreviewContent('Помилка при завантаженні прев’ю');
    }
  };

  const handleClearSearch = () => {
    setSearchResults([]);
  };

  return (
    <div>
      <h1>Файловий менеджер</h1>

      <UploadFileForm />
      <FolderList />
      <FileList />

      <hr />
      <h2>Пошук файлів</h2>
      <SearchBar setSearchResults={setSearchResults} setPreviewContent={setPreviewContent} />

      {searchResults.length > 0 && (
        <div style={{ marginTop: '1rem' }}>
          <h3>Результати пошуку:</h3>
          <ul>
            {searchResults.map(file => (
              <li key={file.id}>
                📄 {file.name} ({file.size} байт)
                <button onClick={() => handlePreview(file.id)}>👁 Переглянути</button>
              </li>
            ))}
          </ul>
          <button onClick={handleClearSearch}>Очистити результати</button>
        </div>
      )}

      <FilePreview content={previewContent} setContent={setPreviewContent} />
    </div>
  );
};

export default Dashboard;
