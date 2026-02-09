import './Question.css';
import { useState } from 'react';
import Sidebar from "../../components/Sidebar/Sidebar.jsx";
import AxiosConfig from "../../service/AxiosConfig.js";
import { API_ENDPOINTS } from "../../service/ApiEndpoints.js";
import { toast } from "react-toastify";

const Question = () => {
    const [data, setData] = useState({
        title: "",
        description: "",
    });

    const [image, setImage] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleImageChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setImage(URL.createObjectURL(e.target.files[0]));
            setImageFile(e.target.files[0]);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        const formData = new FormData();

        if (imageFile) {
            formData.append('image', imageFile);
        }

        formData.append('request', JSON.stringify(data));

        try {
            for (let pair of formData.entries()) {
                console.log(pair[0], pair[1]);
            }
            const response = await AxiosConfig.post(
                API_ENDPOINTS.CREATE_QUESTION,
                formData
            );

            console.log("Response: ", response.status);
            if (response.status === 201) {
                toast.success('Question successfully added.');
                handleClearQuestion();
            }
        } catch (error) {
            console.error(error);
            toast.error('Question failed to add');
        } finally {
            setIsLoading(false);
        }
    };

    const handleClearQuestion = () => {
        setData({ title: "", description: "" });
        setImage(null);
        setImageFile(null);
    };

    return (
        <div className="explore-container">
            <Sidebar />

            <main className="explore-main">
                <div className="question-form-container">
                    <header className="form-header">
                        <h1 className="page-title">Ask a Public Question</h1>
                        <p className="form-subtitle">
                            Be specific and imagine you’re asking a question to another person.
                        </p>
                    </header>

                    <form className="ask-question-form" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="title">Title</label>
                            <p className="label-desc">
                                Be specific and imagine you’re asking a question to another person.
                            </p>
                            <input
                                type="text"
                                id="title"
                                placeholder="e.g. Is there an R function for finding the index of an element in a vector?"
                                value={data.title}
                                onChange={(e) =>
                                    setData({ ...data, title: e.target.value })
                                }
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="description">Description</label>
                            <p className="label-desc">
                                Include all the information someone would need to answer your question.
                            </p>
                            <textarea
                                id="description"
                                rows="8"
                                placeholder="Write your details here..."
                                value={data.description}
                                onChange={(e) =>
                                    setData({ ...data, description: e.target.value })
                                }
                                required
                            ></textarea>
                        </div>

                        <div className="form-group">
                            <label>
                                Image <span className="optional-tag">(Optional)</span>
                            </label>
                            <div className="image-upload-wrapper">
                                <label htmlFor="image-input" className="image-upload-label">
                                    <i className="bi bi-image"></i>
                                    <span>
                                        {image ? "Change Image" : "  Upload an image to illustrate your problem"}
                                    </span>
                                </label>
                                <input
                                    type="file"
                                    id="image-input"
                                    accept="image/*"
                                    onChange={handleImageChange}
                                    hidden
                                />
                            </div>

                            {image && (
                                <div className="image-preview">
                                    <img src={image} alt="Preview" />
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setImage(null);
                                            setImageFile(null);
                                        }}
                                        className="remove-img-btn"
                                    >
                                        <i className="bi bi-x-circle-fill"></i>
                                    </button>
                                </div>
                            )}
                        </div>

                        <div className="form-actions">
                            <button
                                type="submit"
                                className="btn-post-question"
                                disabled={isLoading}
                            >
                                Post Your Question
                            </button>
                            <button type="button" className="btn-cancel" onClick={handleClearQuestion}>
                                Discard Draft
                            </button>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    );
};

export default Question;
