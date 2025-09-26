// src/pages/Profile.tsx
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../services/axiosInstance";
import "../styles/profile.css";

type ProfileProps = { closeModal?: () => void }; // <-- make optional

interface UserProfile {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
  email: string;
  profilePicture: string;
  currency: string;
  timezone: string;
  preferredLanguage: string;
}

interface UploadResponse {
  url: string;
}

const Profile: React.FC<ProfileProps> = ({ closeModal }) => {
  const navigate = useNavigate();

  const [user, setUser] = useState<UserProfile>({
    firstName: "",
    lastName: "",
    phoneNumber: "",
    address: "",
    email: "",
    profilePicture: "",
    currency: "",
    timezone: "",
    preferredLanguage: "",
  });

  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  // helper so both modal & routed page can close
  const handleClose = () => {
    if (closeModal) closeModal();
    else navigate(-1);
  };

  // Fetch user data when modal opens
  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const response = await axiosInstance.get<UserProfile>("/users/profile");
        setUser(response.data);
      } catch (err: any) {
        console.error(
          "❌ Error fetching user profile:",
          err?.response?.data || err?.message
        );
      }
    };
    fetchUserProfile();
  }, []);

  // Handle Profile Update
  const handleUpdateProfile = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      await axiosInstance.put("/users/profile", user);
      alert("✅ Profile updated successfully!");
      handleClose();                // <-- use unified close
      navigate("/dashboard");
    } catch (err: any) {
      console.error(
        "❌ Error updating profile:",
        err?.response?.data || err?.message
      );
    }
  };

  // Handle Profile Picture Upload
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedFile(e.target.files?.[0] ?? null);
  };

  const handleUpload = async () => {
    if (!selectedFile) return;
    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      const response = await axiosInstance.post<UploadResponse>(
        "/users/upload-profile-picture",
        formData
      );
      setUser({ ...user, profilePicture: response.data.url });
      alert("✅ Profile picture updated successfully!");
    } catch (err: any) {
      console.error(
        "❌ Error uploading profile picture:",
        err?.response?.data || err?.message
      );
    }
  };

  return (
    <div className="modal-overlay" onClick={handleClose}>
      <div className="profile-modal" onClick={(e) => e.stopPropagation()}>
        <button className="close-btn btn" onClick={handleClose} aria-label="Close">
          X
        </button>
        <h2 className="edit-profile">Edit Profile</h2>

        {/* Profile Picture Section */}
        <div className="profile-upload-container">
          <img
            src={user.profilePicture || "/img/user-1.png"}
            alt="Profile"
            className="profile-img"
          />
          <input type="file" accept="image/*" onChange={handleFileChange} />
          <button onClick={handleUpload} disabled={!selectedFile}>
            Upload
          </button>
        </div>

        <form onSubmit={handleUpdateProfile}>
          <input
            type="text"
            placeholder="First Name"
            value={user.firstName}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
              setUser({ ...user, firstName: e.target.value })
            }
          />
          <input
            type="text"
            placeholder="Last Name"
            value={user.lastName}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
              setUser({ ...user, lastName: e.target.value })
            }
          />
          <input
            type="text"
            placeholder="Phone Number"
            value={user.phoneNumber}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
              setUser({ ...user, phoneNumber: e.target.value })
            }
          />
          <input
            type="text"
            placeholder="Address"
            value={user.address}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
              setUser({ ...user, address: e.target.value })
            }
          />

          <select
            value={user.currency}
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
              setUser({ ...user, currency: e.target.value })
            }
          >
            <option value="USD">USD ($)</option>
            <option value="EUR">EUR (€)</option>
            <option value="GBP">GBP (£)</option>
          </select>

          <select
            value={user.timezone}
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
              setUser({ ...user, timezone: e.target.value })
            }
          >
            <option value="UTC">UTC</option>
            <option value="PST">PST</option>
            <option value="EST">EST</option>
          </select>

          <select
            value={user.preferredLanguage}
            onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
              setUser({ ...user, preferredLanguage: e.target.value })
            }
          >
            <option value="en">English</option>
            <option value="fr">French</option>
            <option value="es">Spanish</option>
          </select>

          <input type="email" placeholder="Email" value={user.email} readOnly />

          <button type="submit">Update Profile</button>
        </form>
      </div>
    </div>
  );
};

export default Profile;




// // src/pages/Profile.tsx
// import { useState, useEffect } from "react";
// import { useNavigate } from "react-router-dom";
// import axiosInstance from "../services/axiosInstance";
// import "../styles/profile.css";

// type ProfileProps = { closeModal?: () => void }; // <-- make optional

// interface UserProfile {
//   firstName: string;
//   lastName: string;
//   phoneNumber: string;
//   address: string;
//   email: string;
//   profilePicture: string;
//   currency: string;
//   timezone: string;
//   preferredLanguage: string;
// }

// interface UploadResponse {
//   url: string;
// }

// const Profile: React.FC<ProfileProps> = ({ closeModal }) => {
//   const navigate = useNavigate();

//   const [user, setUser] = useState<UserProfile>({
//     firstName: "",
//     lastName: "",
//     phoneNumber: "",
//     address: "",
//     email: "",
//     profilePicture: "",
//     currency: "",
//     timezone: "",
//     preferredLanguage: "",
//   });

//   const [selectedFile, setSelectedFile] = useState<File | null>(null);

//   // helper so both modal & routed page can close
//   const handleClose = () => {
//     if (closeModal) closeModal();
//     else navigate(-1);
//   };

//   // Fetch user data when modal opens
//   useEffect(() => {
//     const fetchUserProfile = async () => {
//       try {
//         const response = await axiosInstance.get<UserProfile>("/users/profile");
//         setUser(response.data);
//       } catch (err: any) {
//         console.error(
//           "❌ Error fetching user profile:",
//           err?.response?.data || err?.message
//         );
//       }
//     };
//     fetchUserProfile();
//   }, []);

//   // Handle Profile Update
//   const handleUpdateProfile = async (e: React.FormEvent<HTMLFormElement>) => {
//     e.preventDefault();
//     try {
//       await axiosInstance.put("/users/profile", user);
//       alert("✅ Profile updated successfully!");
//       handleClose();                // <-- use unified close
//       navigate("/dashboard");
//     } catch (err: any) {
//       console.error(
//         "❌ Error updating profile:",
//         err?.response?.data || err?.message
//       );
//     }
//   };

//   // Handle Profile Picture Upload
//   const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
//     setSelectedFile(e.target.files?.[0] ?? null);
//   };

//   const handleUpload = async () => {
//     if (!selectedFile) return;
//     const formData = new FormData();
//     formData.append("file", selectedFile);

//     try {
//       const response = await axiosInstance.post<UploadResponse>(
//         "/users/upload-profile-picture",
//         formData
//       );
//       setUser({ ...user, profilePicture: response.data.url });
//       alert("✅ Profile picture updated successfully!");
//     } catch (err: any) {
//       console.error(
//         "❌ Error uploading profile picture:",
//         err?.response?.data || err?.message
//       );
//     }
//   };

//   return (
//     <div className="modal-overlay" onClick={handleClose}>
//       <div className="profile-modal" onClick={(e) => e.stopPropagation()}>
//         <button className="close-btn btn" onClick={handleClose} aria-label="Close">
//           X
//         </button>
//         <h2 className="edit-profile">Edit Profile</h2>

//         {/* Profile Picture Section */}
//         <div className="profile-upload-container">
//           <img
//             src={user.profilePicture || "/img/user-1.png"}
//             alt="Profile"
//             className="profile-img"
//           />
//           <input type="file" accept="image/*" onChange={handleFileChange} />
//           <button onClick={handleUpload} disabled={!selectedFile}>
//             Upload
//           </button>
//         </div>

//         <form onSubmit={handleUpdateProfile}>
//           <input
//             type="text"
//             placeholder="First Name"
//             value={user.firstName}
//             onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
//               setUser({ ...user, firstName: e.target.value })
//             }
//           />
//           <input
//             type="text"
//             placeholder="Last Name"
//             value={user.lastName}
//             onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
//               setUser({ ...user, lastName: e.target.value })
//             }
//           />
//           <input
//             type="text"
//             placeholder="Phone Number"
//             value={user.phoneNumber}
//             onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
//               setUser({ ...user, phoneNumber: e.target.value })
//             }
//           />
//           <input
//             type="text"
//             placeholder="Address"
//             value={user.address}
//             onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
//               setUser({ ...user, address: e.target.value })
//             }
//           />

//           <select
//             value={user.currency}
//             onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
//               setUser({ ...user, currency: e.target.value })
//             }
//           >
//             <option value="USD">USD ($)</option>
//             <option value="EUR">EUR (€)</option>
//             <option value="GBP">GBP (£)</option>
//           </select>

//           <select
//             value={user.timezone}
//             onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
//               setUser({ ...user, timezone: e.target.value })
//             }
//           >
//             <option value="UTC">UTC</option>
//             <option value="PST">PST</option>
//             <option value="EST">EST</option>
//           </select>

//           <select
//             value={user.preferredLanguage}
//             onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
//               setUser({ ...user, preferredLanguage: e.target.value })
//             }
//           >
//             <option value="en">English</option>
//             <option value="fr">French</option>
//             <option value="es">Spanish</option>
//           </select>

//           <input type="email" placeholder="Email" value={user.email} readOnly />

//           <button type="submit">Update Profile</button>
//         </form>
//       </div>
//     </div>
//   );
// };

// export default Profile;
