import axios from '../api/axios';
import fileService from '../services/fileService';
jest.mock('../api/axios');

test('отримує список файлів', async () => {
  axios.get.mockResolvedValue({ data: [{ id: 1, name: 'file1.txt' }] });
  const files = await fileService.getFiles(1);
  expect(files.length).toBe(1);
  expect(files[0].name).toBe('file1.txt');
});
