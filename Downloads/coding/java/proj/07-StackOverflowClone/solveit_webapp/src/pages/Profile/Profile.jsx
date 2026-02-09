import './Profile.css';
import { useState, useEffect, useContext, useCallback } from 'react';
import Sidebar from "../../components/Sidebar/Sidebar.jsx";
import { AppContext } from "../../context/AppContext.jsx";
import AxiosConfig from "../../service/AxiosConfig.js";
import { API_ENDPOINTS } from "../../service/ApiEndpoints.js";
import { formatTimePost } from "../../util/UtilMethod.js";

const Profile = () => {
    const [userQuestions, setUserQuestions] = useState([]);
    const [loading, setLoading] = useState(true);

    const { user } = useContext(AppContext);

    const userData = {
        name: user?.name,
        username: user?.username,
        bio: user?.bio,
        stats: {
            questions: user?.questions || 0,
            answers: user?.answers || 0
        }
    };

    const fetchUserQuestions = useCallback(async () => {
        try {
            setLoading(true);
            const response = await AxiosConfig.get(API_ENDPOINTS.GET_QUESTIONS_CURRENT_USER);
            if (response.status === 200) {
                const data = response.data.content;
                setUserQuestions(data);
            }
        } catch (error) {
            console.error("Error fetching questions:", error);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (user) {
            fetchUserQuestions();
        }
    }, [user, fetchUserQuestions]);

    const handleLikesQuestion = async (e, questionId) => {
        e.stopPropagation();
        console.log("Sample Questions", userQuestions);
        console.log(questionId);
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.LIKE_QUESTION(questionId));
            if (response.status === 201 || response.status === 200) {
                console.log("API CALLED");
                fetchUserQuestions();
            }
        } catch (error) {
            console.error("Error liking question:", error);
        }
    };

    const handleUnLikesQuestion = async (e, questionId) => {
        e.stopPropagation();
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.UNLIKE_QUESTION(questionId));
            if (response.status === 201 || response.status === 200) {
                fetchUserQuestions();
            }
        } catch (error) {
            console.error("Error unliking question:", error);
        }
    };

    const handleQuestionClick = (questionId) => {
        console.log(questionId);
    };

    if (!user) {
        return (
            <div className="explore-container">
                <Sidebar />
                <main className="explore-main">
                    <div className="loading-state">
                        <div className="spinner"></div>
                        <p>Loading profile...</p>
                    </div>
                </main>
            </div>
        );
    }

    return (
        <div className="explore-container">
            <Sidebar />

            <main className="explore-main">
                <div className="profile-header-section">
                    <div className="profile-top">
                        <div className="profile-avatar-large">
                            <i className="bi bi-person-circle"></i>
                        </div>
                        <div className="profile-text-info">
                            <h1 className="profile-real-name">{userData.name}</h1>
                            <p className="profile-handle">{userData.username}</p>
                            <p className="profile-bio">{userData.bio}</p>
                        </div>
                    </div>

                    <div className="profile-stats-bar">
                        <div className="stat-card">
                            <span className="stat-value">{userData.stats.questions}</span>
                            <span className="stat-label">Questions</span>
                        </div>
                        <div className="stat-card">
                            <span className="stat-value">{userData.stats.answers}</span>
                            <span className="stat-label">Answers</span>
                        </div>
                    </div>
                </div>

                <hr className="profile-divider" />
                <h2 className="section-title">My Questions</h2>

                <div className="questions-feed">
                    {loading && userQuestions.length === 0 ? (
                        <div className="loading-state">
                            <div className="spinner"></div>
                        </div>
                    ) : userQuestions.length === 0 ? (
                        <div className="empty-state">
                            <p>You haven't asked any questions yet.</p>
                        </div>
                    ) : (
                        userQuestions.map((question) => (
                            <div
                                key={question.id}
                                className="question-card"
                                onClick={() => handleQuestionClick(question.id)}
                            >
                                <div className="question-header">
                                    <div className="author-info">
                                        <div className="author-details">
                                            <span className="author-name">{userData.name}</span>
                                            <span className="author-username">{userData.username}</span>
                                        </div>
                                    </div>
                                    <span className="time-ago">{formatTimePost(question.createdAt)}</span>
                                </div>

                                <h3 className="question-title">{question.title}</h3>

                                {question.description && (
                                    <p className="question-content">{question.description}</p>
                                )}

                                {question.imageUrl && (
                                    <div className="question-image">
                                        <img src={question.imageUrl} alt="Question visual" />
                                    </div>
                                )}

                                <div className="question-stats">
                                    <div className="stat-item">
                                        <i className="bi bi-chat-left-text"></i>
                                        <span>{question.answers ?? 0}</span>
                                    </div>

                                    {/* Like Logic: Maps to Blue in CSS */}
                                    <div
                                        className={`stat-item likes ${question.liked ? 'active' : ''}`}
                                        onClick={(e) => handleLikesQuestion(e, question.id)}
                                    >
                                        <i className={`bi ${question.liked ? 'bi-hand-thumbs-up-fill' : 'bi-hand-thumbs-up'}`}></i>
                                        <span>{question.likes || 0}</span>
                                    </div>

                                    {/* Unlike Logic: Maps to Red in CSS */}
                                    <div
                                        className={`stat-item dislikes ${question.unliked ? 'active' : ''}`}
                                        onClick={(e) => handleUnLikesQuestion(e, question.id)}
                                    >
                                        <i className={`bi ${question.unliked ? 'bi-hand-thumbs-down-fill' : 'bi-hand-thumbs-down'}`}></i>
                                        <span>{question.unlikes || 0}</span>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </main>
        </div>
    );
};

export default Profile;