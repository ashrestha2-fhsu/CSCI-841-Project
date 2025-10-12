
import { useEffect, useState } from "react";
import axiosInstance from "../services/axiosInstance";
import "../styles/categories.css";

type CategoryType = "INCOME" | "EXPENSE";

interface UserProfile {
  userId: number;
}

interface CategoryItem {
  categoryId: number;
  name: string;
  type: CategoryType;
  icon: string;
  colorCode: string;
  dateCreated?: string | null;
  deleted?: boolean;
}

interface CategoryFormData {
  name: string;
  type: CategoryType;
  icon: string;
  colorCode: string;
}

const Category: React.FC = () => {
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [showForm, setShowForm] = useState<boolean>(false);
  const [editCategory, setEditCategory] = useState<CategoryItem | null>(null);
  const [userId, setUserId] = useState<number | null>(null);

  const [formData, setFormData] = useState<CategoryFormData>({
    name: "",
    type: "EXPENSE",
    icon: "",
    colorCode: "#000000",
  });

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await axiosInstance.get<UserProfile>("/users/profile");
        setUserId(res.data.userId);
      } catch (err) {
        console.error("❌ Error fetching user profile:", err);
      }
    };
    fetchUser();
  }, []);

  useEffect(() => {
    if (userId) fetchCategories();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userId]);

  const fetchCategories = async () => {
    try {
      const res = await axiosInstance.get<CategoryItem[]>(
        `/categories/user/${userId}?includeDeleted=true`
      );
      setCategories(res.data);
    } catch (err) {
      console.error("❌ Error fetching categories:", err);
    }
  };

  const openForm = (category: CategoryItem | null = null) => {
    setEditCategory(category);
    setShowForm(true);
    setFormData(
      category
        ? {
            name: category.name,
            type: category.type,
            icon: category.icon,
            colorCode: category.colorCode,
          }
        : {
            name: "",
            type: "EXPENSE",
            icon: "",
            colorCode: "#000000",
          }
    );
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value } as CategoryFormData));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      if (editCategory) {
        await axiosInstance.put(
          `/categories/${editCategory.categoryId}`,
          formData
        );
      } else {
        await axiosInstance.post("/categories", { ...formData, userId });
      }
      setShowForm(false);
      setEditCategory(null);
      fetchCategories();
    } catch (err) {
      console.error("❌ Error saving category:", err);
    }
  };

  const handleDelete = async (id: number) => {
    await axiosInstance.delete(`/categories/${id}`);
    fetchCategories();
  };

  const handleRestore = async (id: number) => {
    await axiosInstance.put(`/categories/${id}/restore`);
    fetchCategories();
  };

  if (!userId) return <p>Loading user...</p>;

  return (
    <div className="category-container">
      <div className="category-header">
        <h2>Categories</h2>
        <button className="add-btn" onClick={() => openForm(null)}>
          ➕ Add Category
        </button>
      </div>

      {showForm && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{editCategory ? "Edit Category" : "Add Category"}</h3>
            <form onSubmit={handleSubmit}>
              <table className="form-table">
                <tbody>
                  <tr>
                    <td>
                      <label>Name:</label>
                    </td>
                    <td>
                      <input
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                      />
                    </td>
                    <td>
                      <label>Type:</label>
                    </td>
                    <td>
                      <select
                        name="type"
                        value={formData.type}
                        onChange={handleChange}
                      >
                        <option value="INCOME">INCOME</option>
                        <option value="EXPENSE">EXPENSE</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <label>Icon:</label>
                    </td>
                    <td>
                      <select
                        name="icon"
                        value={formData.icon}
                        onChange={handleChange}
                      >
                        <option value="">Select Icon</option>
                        <option value="💰">💰 Money</option>
                        <option value="🛒">🛒 Shopping</option>
                        <option value="🏠">🏠 Home</option>
                        <option value="🚗">🚗 Car</option>
                        <option value="🍽">🍽 Food</option>
                        <option value="🎓">🎓 Education</option>
                        <option value="⚡">⚡ Electricity</option>
                      </select>
                    </td>
                    <td>
                      <label>Color:</label>
                    </td>
                    <td>
                      <input
                        type="color"
                        name="colorCode"
                        value={formData.colorCode}
                        onChange={handleChange}
                      />
                    </td>
                  </tr>
                  <tr>
                    <td colSpan={4} className="form-buttons-row">
                      <div className="form-buttons">
                        <button type="submit" className="save-btn">
                          Save
                        </button>
                        <button
                          type="button"
                          onClick={() => {
                            setShowForm(false);
                            setEditCategory(null);
                          }}
                          className="cancel-btn"
                        >
                          Cancel
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </form>
          </div>
        </div>
      )}

      <table className="category-table form-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Icon</th>
            <th>Color</th>
            <th>Date Created</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {categories.map((cat) => (
            <tr
              key={cat.categoryId}
              className={cat.deleted ? "deleted-row" : ""}
            >
              <td>{cat.name}</td>
              <td>{cat.type}</td>
              <td>{cat.icon}</td>
              <td>
                <span
                  style={{
                    display: "inline-block",
                    width: "20px",
                    height: "20px",
                    borderRadius: "50%",
                    backgroundColor: cat.colorCode,
                    border: "1px solid #aaa",
                  }}
                ></span>
              </td>
              <td>
                {cat.dateCreated
                  ? new Date(cat.dateCreated).toLocaleDateString()
                  : "N/A"}
              </td>
              <td>
                {!cat.deleted ? (
                  <>
                    <button className="edit-btn" onClick={() => openForm(cat)}>
                      ✏️ Edit
                    </button>
                    <button
                      className="delete-btn"
                      onClick={() => handleDelete(cat.categoryId)}
                    >
                      🗑 Delete
                    </button>
                  </>
                ) : (
                  <button
                    className="restore"
                    onClick={() => handleRestore(cat.categoryId)}
                  >
                    ♻️ Restore
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Category;
