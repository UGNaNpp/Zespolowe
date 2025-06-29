import { render, screen, act } from '@testing-library/react';
import { ErrorToast } from '@/app/components/errorToast/ErrorToast';
import '@testing-library/jest-dom';

jest.useFakeTimers();

describe('ErrorToast Component', () => {
  const message = 'Something went wrong';
  const onHide = jest.fn();

  beforeEach(() => {
    jest.clearAllTimers();
    jest.clearAllMocks();
  });

  it('renders when show is true', () => {
    render(<ErrorToast message={message} show={true} onHide={onHide} />);
    expect(screen.getByText(message)).toBeInTheDocument();
  });

  it('does not render when show is false', () => {
    render(<ErrorToast message={message} show={false} onHide={onHide} />);
    expect(screen.queryByText(message)).not.toBeInTheDocument();
  });

  it('hides after 5 seconds and calls onHide', () => {
    render(<ErrorToast message={message} show={true} onHide={onHide} />);

    expect(screen.getByText(message)).toBeInTheDocument();

    act(() => {
      jest.advanceTimersByTime(5000);
    });

    expect(onHide).toHaveBeenCalled();
  });
});
