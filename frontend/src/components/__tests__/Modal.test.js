import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import Modal from '../Modal';

describe('Modal Component', () => {
  test('renders modal when isOpen prop is true', () => {
    render(
      <Modal isOpen={true} title="Test Modal">
        <p>Modal content</p>
      </Modal>
    );

    expect(screen.getByText('Test Modal')).toBeInTheDocument();
    expect(screen.getByText('Modal content')).toBeInTheDocument();
  });

  test('does not render modal when isOpen prop is false', () => {
    render(
      <Modal isOpen={false} title="Test Modal">
        <p>Modal content</p>
      </Modal>
    );

    expect(screen.queryByText('Test Modal')).not.toBeInTheDocument();
    expect(screen.queryByText('Modal content')).not.toBeInTheDocument();
  });

  test('calls onClose when close button is clicked', () => {
    const handleClose = jest.fn();
    render(
      <Modal isOpen={true} title="Test Modal" onClose={handleClose}>
        <p>Modal content</p>
      </Modal>
    );

    const closeButton = screen.getByLabelText('Close');
    fireEvent.click(closeButton);
    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  test('calls onClose when overlay is clicked', () => {
    const handleClose = jest.fn();
    render(
      <Modal isOpen={true} title="Test Modal" onClose={handleClose} closeOnOverlayClick={true}>
        <p>Modal content</p>
      </Modal>
    );

    const overlay = screen.getByTestId('modal-overlay');
    fireEvent.click(overlay);
    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  test('does not call onClose when overlay is clicked and closeOnOverlayClick is false', () => {
    const handleClose = jest.fn();
    render(
      <Modal isOpen={true} title="Test Modal" onClose={handleClose} closeOnOverlayClick={false}>
        <p>Modal content</p>
      </Modal>
    );

    const overlay = screen.getByTestId('modal-overlay');
    fireEvent.click(overlay);
    expect(handleClose).not.toHaveBeenCalled();
  });

  test('renders custom header when header prop is provided', () => {
    const customHeader = <div>Custom Header</div>;
    render(
      <Modal isOpen={true} header={customHeader}>
        <p>Modal content</p>
      </Modal>
    );

    expect(screen.getByText('Custom Header')).toBeInTheDocument();
    expect(screen.queryByText('Test Modal')).not.toBeInTheDocument();
  });

  test('renders custom footer when footer prop is provided', () => {
    const customFooter = <button>Custom Footer Button</button>;
    render(
      <Modal isOpen={true} title="Test Modal" footer={customFooter}>
        <p>Modal content</p>
      </Modal>
    );

    expect(screen.getByText('Custom Footer Button')).toBeInTheDocument();
  });

  test('applies size classes correctly', () => {
    render(
      <Modal isOpen={true} title="Test Modal" size="large">
        <p>Modal content</p>
      </Modal>
    );

    const modal = screen.getByTestId('modal-container');
    expect(modal).toHaveClass('modal-large');
  });

  test('applies custom className', () => {
    render(
      <Modal isOpen={true} title="Test Modal" className="custom-modal">
        <p>Modal content</p>
      </Modal>
    );

    const modal = screen.getByTestId('modal-container');
    expect(modal).toHaveClass('custom-modal');
  });

  test('applies centered class when centered prop is true', () => {
    render(
      <Modal isOpen={true} title="Test Modal" centered={true}>
        <p>Modal content</p>
      </Modal>
    );

    const modal = screen.getByTestId('modal-container');
    expect(modal).toHaveClass('modal-centered');
  });

  test('applies scrollable class when scrollable prop is true', () => {
    render(
      <Modal isOpen={true} title="Test Modal" scrollable={true}>
        <p>Modal content</p>
      </Modal>
    );

    const modal = screen.getByTestId('modal-container');
    expect(modal).toHaveClass('modal-scrollable');
  });

  test('applies animation classes correctly', () => {
    render(
      <Modal isOpen={true} title="Test Modal" animation="fade">
        <p>Modal content</p>
      </Modal>
    );

    const modal = screen.getByTestId('modal-container');
    expect(modal).toHaveClass('modal-fade');
  });

  test('calls onClose when Escape key is pressed', () => {
    const handleClose = jest.fn();
    render(
      <Modal isOpen={true} title="Test Modal" onClose={handleClose} closeOnEscape={true}>
        <p>Modal content</p>
      </Modal>
    );

    fireEvent.keyDown(document, { key: 'Escape', code: 'Escape' });
    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  test('does not call onClose when Escape key is pressed and closeOnEscape is false', () => {
    const handleClose = jest.fn();
    render(
      <Modal isOpen={true} title="Test Modal" onClose={handleClose} closeOnEscape={false}>
        <p>Modal content</p>
      </Modal>
    );

    fireEvent.keyDown(document, { key: 'Escape', code: 'Escape' });
    expect(handleClose).not.toHaveBeenCalled();
  });
});
