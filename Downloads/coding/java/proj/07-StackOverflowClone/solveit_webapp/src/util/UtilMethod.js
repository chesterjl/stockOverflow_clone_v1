import {toast} from "react-toastify";

export const formatTimePost = (dateString) => {
    if (!dateString) return 'recently';

    const now = new Date();
    const createdAt = new Date(dateString);
    const diffInMs = now - createdAt;

    const seconds = Math.floor(diffInMs / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const months = Math.floor(days / 30);
    const years = Math.floor(days / 365);

    if (seconds < 60) {
        return 'just now';
    } else if (minutes < 60) {
        return `${minutes} ${minutes === 1 ? 'minute' : 'minutes'} ago`;
    } else if (hours < 24) {
        return `${hours} ${hours === 1 ? 'hour' : 'hours'} ago`;
    } else if (days < 30) {
        return `${days} ${days === 1 ? 'day' : 'days'} ago`;
    } else if (months < 12) {
        return `${months} ${months === 1 ? 'month' : 'months'} ago`;
    } else {
        return `${years} ${years === 1 ? 'year' : 'years'} ago`;
    }
};

export const validateForm = (data) => {
    if (!data.firstName.trim()) {
        toast.error('First name is required');
        return false;
    }
    if (!data.lastName.trim()) {
        toast.error('Last name is required');
        return false;
    }
    if (!data.email.trim()) {
        toast.error('Email is required');
        return false;
    }
    if (!/\S+@\S+\.\S+/.test(data.email)) {
        toast.error('Please enter a valid email address');
        return false;
    }
    if (!data.password) {
        toast.error('Password is required');
        return false;
    }
    if (data.password.length < 6) {
        toast.error('Password must be at least 6 characters long');
        return false;
    }

    if (data.password.length > 20) {  // REMOVE THE ! HERE
        toast.error('Password must be maximum 20 characters long');
        return false;
    }

    if (!data.confirmPassword) {
        toast.error('Please confirm your password');
        return false;
    }
    if (data.password !== data.confirmPassword) {
        toast.error('Passwords do not match');
        return false;
    }

    return true;
}