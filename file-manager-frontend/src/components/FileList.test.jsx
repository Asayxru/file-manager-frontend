import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import FileList from './FileList';
import axios from '../api/axios';

jest.mock('../api/axios');

describe('FileList', () => {
  test('відображає список файлів', async () => {
    const fakeFiles = [
      { id: 1, name: 'file1.txt', size: 123, folderId: null },
      { id: 2, name: 'file2.jpg', size: 456, folderId: null }
    ];
    axios.get.mockResolvedValue({ data: fakeFiles });

    render(<FileList />);

    await waitFor(() => {
      expect(screen.getByText(/file1.txt/i)).toBeInTheDocument();
      expect(screen.getByText(/file2.jpg/i)).toBeInTheDocument();
    });
  });

  test('відображає повідомлення про відсутність файлів', async () => {
    axios.get.mockResolvedValue({ data: [] });

    render(<FileList />);

    await waitFor(() => {
      expect(screen.queryByRole('listitem')).not.toBeInTheDocument();
    });
  });
});
