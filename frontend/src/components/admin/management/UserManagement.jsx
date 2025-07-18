import React, { useState } from "react";
import { FaTrash } from "react-icons/fa";
import { formatDate } from "../utils/utils";

const UserManagement = ({ users, onDeleteUser }) => {
  const [filterRole, setFilterRole] = useState('all');
  
  const filteredUsers = filterRole === 'all'
    ? users
    : users.filter(user => user.role === filterRole);

  return (
    <div>
      <h4 className="mb-4">Benutzerverwaltung</h4>
      
      <div className="card mb-4">
        <div className="card-body">
          <div className="d-flex justify-content-end mb-3">
            <div className="btn-group" role="group" aria-label="User filter">
              <button 
                className={`btn ${filterRole === 'all' ? 'btn-primary' : 'btn-light border'}`}
                onClick={() => setFilterRole('all')}
              >
                <i className="bi bi-people me-1"></i>
                Alle
              </button>
              <button 
                className={`btn ${filterRole === 'USER' ? 'btn-primary' : 'btn-light border'}`}
                onClick={() => setFilterRole('USER')}
              >
                <i className="bi bi-person me-1"></i>
                User
              </button>
              <button 
                className={`btn ${filterRole === 'ADMIN' ? 'btn-primary' : 'btn-light border'}`}
                onClick={() => setFilterRole('ADMIN')}
              >
                <i className="bi bi-shield-check me-1"></i>
                Admin
              </button>
            </div>
          </div>

          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Benutzername</th>
                  <th>E-Mail</th>
                  <th>Rolle</th>
                  <th>Registriert am</th>
                  <th>Aktionen</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map(user => (
                  <tr key={user.id}>
                    <td>{user.username}</td>
                    <td>{user.email}</td>
                    <td>
                      <span className={`badge ${user.role === 'ADMIN' ? 'bg-danger' : 'bg-primary'}`}>
                        {user.role}
                      </span>
                    </td>
                    <td>{formatDate(user.joinedAt)}</td>
                    <td>
                      {user.role !== 'ADMIN' && (
                        <button
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => {
                            if (window.confirm(`Möchten Sie den Benutzer "${user.username}" wirklich löschen?`)) {
                              onDeleteUser(user.id);
                            }
                          }}
                        >
                          <FaTrash /> Löschen
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;