import React from 'react';

const FilePreview = ({ content, setContent }) => {
  if (!content) return null;

  return (
    <div style={{
      border: '1px solid #ccc',
      padding: '1rem',
      marginTop: '1rem',
      borderRadius: '5px',
      backgroundColor: '#f9f9f9'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h3>Попередній перегляд файлу</h3>
        <button onClick={() => setContent('')} style={{ fontSize: '1rem', cursor: 'pointer' }}>❌ Закрити</button>
      </div>
      <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>
        {content}
      </pre>
    </div>
  );
};

export default FilePreview;
