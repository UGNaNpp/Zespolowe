import { render, screen, fireEvent } from '@testing-library/react';
import { DeviceEditForm } from '@/app/components/deviceForm/deviceForm';
import '@testing-library/jest-dom';

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

describe('DeviceEditForm Component', () => {
  const mockProps = {
    modify: false,
    onClose: jest.fn(),
    dict: {
      "addNew": "Add new device",
      "modify": "Edit device",
      "name": "Name",
      "ip": "IP address",
      "mac": "MAC address",
      "resolution": "Resolution",
      "recordingMode": "Recording mode",
      "recordingVideo": "Recording video",
      "save": "Save",
      "add": "Add",
      "required": "Required",
      "maxName": "Name must be at most 20 characters long",
      "invalidIP": "Invalid IP address",
      "invalidMAC": "Invalid MAC address",
      "widthPositive": "Width must be positive",
      "widthMax": "Maximum width is 3840 (4K)",
      "heightPositive": "Height must be positive",
      "heightMax": "Maximum height is 2160 (4K)",
      "updated": "Updated the device",
      "added": "Added new device"
    },
    ApiErrorsDict: {
      "unknown": "Request error occurred",
      "400": "Bad request",
      "401": "Unauthorized",
      "403": "Forbidden",
      "404": "Resource not found",
      "500": "Server error"
    }
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders all input fields and labels', () => {
    render(<DeviceEditForm {...mockProps} />);
    
    expect(screen.getByText(mockProps.dict.addNew)).toBeInTheDocument();
    expect(screen.getByLabelText(mockProps.dict.name)).toBeInTheDocument();
    expect(screen.getByLabelText(mockProps.dict.ip)).toBeInTheDocument();
    expect(screen.getByLabelText(mockProps.dict.mac)).toBeInTheDocument();
    expect(screen.getByText(mockProps.dict.resolution)).toBeInTheDocument();
    expect(screen.getByText(mockProps.dict.recordingMode)).toBeInTheDocument();
    expect(screen.getByText(mockProps.dict.recordingVideo)).toBeInTheDocument();
  });

  it('calls onClose when X button is clicked', () => {
    render(<DeviceEditForm {...mockProps} />);
    const closeBtn = screen.getByRole('button', { name: '' });
    fireEvent.click(closeBtn);
    expect(mockProps.onClose).toHaveBeenCalled();
  });
});
