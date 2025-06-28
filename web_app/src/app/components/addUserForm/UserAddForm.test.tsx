import { render, screen, fireEvent } from '@testing-library/react';
import { UserAddForm } from '@/app/components/addUserForm/UserAddForm';
import '@testing-library/jest-dom';

describe('UserAddForm Component', () => {
  const mockProps = {
    dict: {
      "addUser": "Add user",
      "login": "Username",
      "add": "Add",
      "cancel": "Cancel",
      "addedUser": "Added new user",
      "notFound": "User not found",
      "loginRequired": "Login is required",
      "loginNotOnlySpaces": "Login cannot be only spaces"
    },
    ApiErrorsDict: {
      "unknown": "Request error occurred",
      "400": "Bad request",
      "401": "Unauthorized",
      "403": "Forbidden",
      "404": "Resource not found",
      "500": "Server error"
    },
    onClose: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders input, labels and buttons', () => {
    render(<UserAddForm {...mockProps} />);

    expect(screen.getByText(mockProps.dict.addUser)).toBeInTheDocument();
    expect(screen.getByLabelText(mockProps.dict.login)).toBeInTheDocument();
    expect(screen.getByText(mockProps.dict.add)).toBeInTheDocument();
    expect(screen.getByText(mockProps.dict.cancel)).toBeInTheDocument();
  });

  it('shows validation error if input is empty and blurred', async () => {
    render(<UserAddForm {...mockProps} />);
    const input = screen.getByLabelText(mockProps.dict.login);
    fireEvent.blur(input);
    fireEvent.change(input, { target: { value: '   ' } });

    fireEvent.blur(input);

    expect(await screen.findByText(/cannot be only spaces/i)).toBeInTheDocument();
  });

  it('calls onClose when Cancel button is clicked', () => {
    render(<UserAddForm {...mockProps} />);
    const cancelBtn = screen.getByText(mockProps.dict.cancel);
    fireEvent.click(cancelBtn);
    expect(mockProps.onClose).toHaveBeenCalled();
  });
});
