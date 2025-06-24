import React, { useEffect, useState } from 'react';
import axios from '../api/axios';

const UploadFileForm = () => {
  const [file, setFile] = useState(null);
  const [folderId, setFolderId] = useState('');
  const [folders, setFolders] = useState([]);
  const [filesInFolder, setFilesInFolder] = useState([]);

  useEffect(() => {
    // Отримуємо всі папки
    const fetchFolders = async () => {
      const res = await axios.get('/folders');
      setFolders(res.data);
    };
    fetchFolders();
  }, []);

  useEffect(() => {
    // Отримуємо файли в обраній папці
    const fetchFiles = async () => {
      if (folderId) {
        const res = await axios.get('/files');
        const filtered = res.data.filter(file => file.folderId === Number(folderId));
        setFilesInFolder(filtered);
      } else {
        setFilesInFolder([]);
      }
    };
    fetchFiles();
  }, [folderId]);

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('folderId', folderId);

    await axios.post('/files/upload', formData);
    window.location.reload();
  };

  return (
    <div>
      <h2>Завантажити файл</h2>
      <input type="file" onChange={e => setFile(e.target.files[0])} />

      <div>
        <label>Оберіть папку:</label>
        <select value={folderId} onChange={e => setFolderId(e.target.value)}>
          <option value="">Без папки</option>
          {folders.map(folder => (
            <option key={folder.id} value={folder.id}>
              {folder.name}
            </option>
          ))}
        </select>
      </div>

      {folderId && (
        <div style={{ marginTop: '1rem' }}>
          <h4>Файли у цій папці:</h4>
          <ul>
            {filesInFolder.map(file => (
              <li key={file.id}>{file.name}</li>
            ))}
          </ul>
        </div>
      )}

      <button onClick={handleUpload} disabled={!file}>Завантажити</button>
    </div>
  );
};

export default UploadFileForm;
