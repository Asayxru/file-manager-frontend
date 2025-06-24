import React, { useEffect, useState } from 'react';
import axios from '../api/axios';

const FileList = () => {
  const [files, setFiles] = useState([]);

  useEffect(() => {
    axios.get('/files').then(res => {
      // Фільтруємо тільки ті файли, що не мають folderId
      const standaloneFiles = res.data.filter(file => !file.folderId);
      setFiles(standaloneFiles);
    });
  }, []);

  const deleteFile = (id) => {
    if (window.confirm('Видалити файл?')) {
      axios.delete(`/files/${id}`).then(() => window.location.reload());
    }
  };

  return (
    <div>
      <h2>Файли без папки</h2>
      <ul>
        {files.map(file => (
          <li key={file.id}>
            {file.name} ({file.size} байт)
            <a
              href={`http://localhost:8080/api/files/download/${file.name}`}
              download
            >
              ⬇️
            </a>
            <button onClick={() => deleteFile(file.id)}>🗑</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default FileList;
