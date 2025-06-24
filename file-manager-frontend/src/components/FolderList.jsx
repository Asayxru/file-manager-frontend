import React, { useEffect, useState } from 'react';
import axios from '../api/axios';

const FolderNode = ({
  folder,
  expandedFolders,
  toggleFolder,
  files,
  folders,
  level = 0,
  onStartEdit,
  onDelete,
  onCreateSubfolder,
  editId,
  editName,
  setEditName,
  onSubmitEdit,
  onDropFile
}) => {
  const isExpanded = expandedFolders.includes(folder.id);
  const subfolders = folders.filter(f => f.parentFolderId === folder.id);
  const filesInFolder = files.filter(f => f.folderId === folder.id);
  const [isDragOver, setIsDragOver] = useState(false);

  const handlePreview = async (fileId) => {
    try {
      const res = await axios.get(`/files/preview/by-id/${fileId}`);
      alert(res.data); // можна замінити на модалку
    } catch (err) {
      alert("Помилка перегляду файлу: " + err.message);
    }
  };

  return (
    <li
      style={{
        marginLeft: level * 20,
        backgroundColor: isDragOver ? '#d0eaff' : 'transparent',
        borderRadius: '4px',
        padding: '2px'
      }}
      onDragOver={e => {
        e.preventDefault();
        setIsDragOver(true);
      }}
      onDragLeave={() => setIsDragOver(false)}
      onDrop={e => {
        e.preventDefault();
        setIsDragOver(false);
        const fileId = e.dataTransfer.getData('fileId');
        if (fileId) {
          onDropFile(parseInt(fileId), folder.id);
        }
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
        <button onClick={() => toggleFolder(folder.id)}>
          {isExpanded ? '📂' : '📁'}
        </button>
        {editId === folder.id ? (
          <>
            <input value={editName} onChange={e => setEditName(e.target.value)} />
            <button onClick={onSubmitEdit}>💾</button>
          </>
        ) : (
          <>
            {folder.name}
            <button onClick={() => onStartEdit(folder.id, folder.name)}>✏️</button>
          </>
        )}
        <button onClick={() => onDelete(folder.id)}>🗑</button>
        <button onClick={() => onCreateSubfolder(folder.id)}>➕</button>
      </div>

      {isExpanded && (
        <>
          {filesInFolder.length > 0 && (
            <ul>
              {filesInFolder.map(file => (
                <li
                  key={file.id}
                  draggable
                  onDragStart={e => e.dataTransfer.setData('fileId', file.id)}
                >
                  📄 {file.name} ({file.size} байт)
                  <a href={`http://localhost:8080/api/files/download/${file.name}`} download>⬇️</a>
                  <button onClick={() => {
                    if (window.confirm('Видалити файл?')) {
                      axios.delete(`/files/${file.id}`).then(() => window.location.reload());
                    }
                  }}>🗑</button>
                  <button onClick={() => handlePreview(file.id)}>👁</button>
                </li>
              ))}
            </ul>
          )}
          {subfolders.length > 0 && (
            <ul>
              {subfolders.map(sub => (
                <FolderNode
                  key={sub.id}
                  folder={sub}
                  expandedFolders={expandedFolders}
                  toggleFolder={toggleFolder}
                  files={files}
                  folders={folders}
                  level={level + 1}
                  onStartEdit={onStartEdit}
                  onDelete={onDelete}
                  onCreateSubfolder={onCreateSubfolder}
                  editId={editId}
                  editName={editName}
                  setEditName={setEditName}
                  onSubmitEdit={onSubmitEdit}
                  onDropFile={onDropFile}
                />
              ))}
            </ul>
          )}
        </>
      )}
    </li>
  );
};



const FolderList = () => {
  const [folders, setFolders] = useState([]);
  const [files, setFiles] = useState([]);
  const [expandedFolders, setExpandedFolders] = useState([]);
  const [newName, setNewName] = useState('');
  const [editId, setEditId] = useState(null);
  const [editName, setEditName] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const [folderRes, fileRes] = await Promise.all([
      axios.get('/folders'),
      axios.get('/files')
    ]);
    setFolders(folderRes.data);
    setFiles(fileRes.data);
  };

  const createFolder = async () => {
    if (!newName.trim()) return;
    await axios.post('/folders', { name: newName });
    setNewName('');
    fetchData();
  };

  const createSubfolder = async (parentFolderId) => {
    const name = prompt('Назва підпапки:');
    if (name) {
      await axios.post('/folders', { name, parentFolderId });
      fetchData();
      setExpandedFolders(prev => [...new Set([...prev, parentFolderId])]);
    }
  };

  const deleteFolder = async (id) => {
    if (window.confirm('Видалити цю папку?')) {
      await axios.delete(`/folders/${id}`);
      fetchData();
    }
  };

  const toggleFolder = (id) => {
    setExpandedFolders(prev =>
      prev.includes(id) ? prev.filter(fid => fid !== id) : [...prev, id]
    );
  };

  const startEdit = (id, name) => {
    setEditId(id);
    setEditName(name);
  };

  const submitEdit = async () => {
    if (editName.trim()) {
      await axios.put(`/folders/${editId}`, { name: editName });
      setEditId(null);
      setEditName('');
      fetchData();
    }
  };

  const onDropFile = async (fileId, targetFolderId) => {
    const file = files.find(f => f.id === fileId);
    if (!file || file.folderId === targetFolderId) return;

    await axios.put(`/files/${fileId}`, {
      ...file,
      folderId: targetFolderId,
      visibility: file.visibility || 'PRIVATE',
      ownerId: file.ownerId,
      name: file.name,
      path: file.path,
      size: file.size
    });
    fetchData();
  };

  const rootFolders = folders.filter(f => f.parentFolderId === null);

  return (
    <div>
      <h2>Папки</h2>
      <div style={{ display: 'flex', gap: '0.5rem' }}>
        <input value={newName} onChange={e => setNewName(e.target.value)} placeholder="Нова папка" />
        <button onClick={createFolder}>Створити</button>
      </div>
      <ul>
        {rootFolders.map(folder => (
          <FolderNode
            key={folder.id}
            folder={folder}
            expandedFolders={expandedFolders}
            toggleFolder={toggleFolder}
            files={files}
            folders={folders}
            onStartEdit={startEdit}
            onDelete={deleteFolder}
            onCreateSubfolder={createSubfolder}
            editId={editId}
            editName={editName}
            setEditName={setEditName}
            onSubmitEdit={submitEdit}
            onDropFile={onDropFile}
          />
        ))}
      </ul>
    </div>
  );
};

export default FolderList;
