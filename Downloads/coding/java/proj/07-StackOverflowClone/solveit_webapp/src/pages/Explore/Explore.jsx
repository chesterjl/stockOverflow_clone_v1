import './Explore.css';
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Sidebar from "../../components/Sidebar/Sidebar.jsx";
import AxiosConfig from "../../service/AxiosConfig.js";
import { API_ENDPOINTS } from "../../service/ApiEndpoints.js";
import { formatTimePost } from "../../util/UtilMethod.js";

const Explore = () => {
    const navigate = useNavigate();
    const [questions, setQuestions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterType, setFilterType] = useState('feed'); // feed, recent, popular

    // Fetch questions based on filter type
    useEffect(() => {
        fetchQuestions();
    }, [filterType]);

    const fetchQuestions = async () => {
        try {
            setLoading(true);
            let response;

            switch (filterType) {
                case 'feed':
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS);
                    break;
                case 'recent':
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS_RECENT);
                    break;
                case 'popular':
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS_POPULAR);
                    break;
                default:
                    response = await AxiosConfig.get(API_ENDPOINTS.GET_FEEDS);
            }

            if (response.status === 200) {
                const questionsData = response.data.content;
                setQuestions(questionsData);
            }

            console.log("Questions: ", response.data.content);
        } catch (error) {
            console.error("Error fetching questions:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleLikesQuestion = async (e, questionId) => {
        e.stopPropagation();
        console.log("Liking question ID:", questionId);
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.LIKE_QUESTION(questionId));            if (response.status === 201 || response.status === 200) {
                fetchQuestions();
            }
        } catch (error) {
            console.error("Error liking question:", error);
        }
    };

    const handleUnLikesQuestion = async (e, questionId) => {
        e.stopPropagation();
        try {
            const response = await AxiosConfig.post(API_ENDPOINTS.UNLIKE_QUESTION(questionId));            if (response.status === 201 || response.status === 200) {
                fetchQuestions();
            }
        } catch (error) {
            console.error("Error unliking question:", error);
        }
    };

    const handleSearch = (e) => {
        setSearchQuery(e.target.value);
    };

    const handleQuestionClick = (questionId) => {
        navigate(`/question/${questionId}`);
    };

    const handleAskQuestion = () => {
        navigate('/questions');
    };

    const handleFilterChange = (newFilter) => {
        setFilterType(newFilter);
    };

    const filteredQuestions = questions.filter(q =>
        q.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (q.description && q.description.toLowerCase().includes(searchQuery.toLowerCase()))
    );

    return (
        <div className="explore-container">
            <Sidebar />

            <main className="explore-main">
                <div className="explore-header">
                    <div className="header-top">
                        <h1 className="page-title">Explore Questions</h1>
                        <button className="btn-ask-question" onClick={handleAskQuestion}>
                            <i className="bi bi-plus-circle"></i>
                            Ask Question
                        </button>
                    </div>

                    <div className="search-container">
                        <i className="bi bi-search search-icon"></i>
                        <input
                            type="text"
                            className="search-input"
                            placeholder="Search questions..."
                            value={searchQuery}
                            onChange={handleSearch}
                        />
                    </div>

                    <div className="filter-tabs">
                        <button
                            className={`filter-tab ${filterType === 'feed' ? 'active' : ''}`}
                            onClick={() => handleFilterChange('feed')}
                        >
                            <i className="bi bi-grid"></i>
                            Feed
                        </button>
                        <button
                            className={`filter-tab ${filterType === 'recent' ? 'active' : ''}`}
                            onClick={() => handleFilterChange('recent')}
                        >
                            <i className="bi bi-clock"></i>
                            Recent
                        </button>
                        <button
                            className={`filter-tab ${filterType === 'popular' ? 'active' : ''}`}
                            onClick={() => handleFilterChange('popular')}
                        >
                            <i className="bi bi-fire"></i>
                            Popular
                        </button>
                    </div>
                </div>

                <div className="questions-feed">
                    {loading ? (
                        <div className="loading-state">
                            <div className="spinner"></div>
                            <p>Loading questions...</p>
                        </div>
                    ) : filteredQuestions.length === 0 ? (
                        <div className="empty-state">
                            <i className="bi bi-inbox"></i>
                            <h3>No questions found</h3>
                            <p>Try adjusting your search or be the first to ask a question!</p>
                            <button className="btn-primary" onClick={handleAskQuestion}>
                                Ask Question
                            </button>
                        </div>
                    ) : (
                        filteredQuestions.map((question) => (
                            <div
                                key={question.id}
                                className="question-card"
                                onClick={() => handleQuestionClick(question.id)}
                            >
                                <div className="question-header">
                                    <div className="author-info">
                                        <div className="author-details">
                                            <span className="author-name">{question.name || 'Anonymous'}</span>
                                            <span className="author-username">{question.username || 'user'}</span>
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

                                    {/* Updated Like Button Logic */}
                                    <div
                                        className={`stat-item likes ${question.liked ? 'active' : ''}`}
                                        onClick={(e) => handleLikesQuestion(e, question.id)}
                                    >
                                        <i className={`bi ${question.liked ? 'bi-hand-thumbs-up-fill' : 'bi-hand-thumbs-up'}`}></i>
                                        <span>{question.likes ?? 0}</span>
                                    </div>

                                    {/* Updated Unlike Button Logic */}
                                    <div
                                        className={`stat-item dislikes ${question.unliked ? 'active' : ''}`}
                                        onClick={(e) => handleUnLikesQuestion(e, question.id)}
                                    >
                                        <i className={`bi ${question.unliked ? 'bi-hand-thumbs-down-fill' : 'bi-hand-thumbs-down'}`}></i>
                                        <span>{question.unlikes ?? 0}</span>
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

export default Explore;