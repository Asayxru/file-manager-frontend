import { render, screen, fireEvent } from '@testing-library/react';
import SearchBar from './SearchBar';
import React from 'react';


test('рендерить поле пошуку і кнопку', () => {
 render(<SearchBar setSearchResults={() => {}} setPreviewContent={() => {}} />);

  expect(screen.getByPlaceholderText(/пошук/i)).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /очистити/i })).toBeInTheDocument();
});

test('очищає поле при натисканні на кнопку Очистити', () => {
  render(<SearchBar setSearchResults={() => {}} setPreviewContent={() => {}} />);

  const input = screen.getByPlaceholderText(/пошук/i);
  fireEvent.change(input, { target: { value: 'тест' } });
  fireEvent.click(screen.getByRole('button', { name: /очистити/i }));
  expect(input.value).toBe('');
});
