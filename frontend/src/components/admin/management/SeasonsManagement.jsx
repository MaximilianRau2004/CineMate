import React from "react";
import { FaArrowLeft, FaPlus, FaEdit, FaTrash, FaPlayCircle } from "react-icons/fa";

const SeasonsManagement = ({ series, seasons, onAddSeason, onEditSeason, onDeleteSeason, onViewEpisodes, onBack }) => (
  <div>
    <div className="d-flex justify-content-between align-items-center mb-4">
      <div className="d-flex align-items-center">
        <button className="btn btn-outline-secondary me-3" onClick={onBack}>
          <FaArrowLeft /> Zurück
        </button>
        <h4>Staffeln für "{series.title}"</h4>
      </div>
      <button className="btn btn-primary" onClick={onAddSeason}>
        <FaPlus /> Staffel hinzufügen
      </button>
    </div>

    <div className="card">
      <div className="card-body">
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Staffel Nr.</th>
                <th>Episoden</th>
                <th>Trailer URL</th>
                <th>Aktionen</th>
              </tr>
            </thead>
            <tbody>
              {seasons.map((season, index) => (
                <tr key={`season-${season.seasonNumber}-${index}`}>
                  <td>Staffel {season.seasonNumber}</td>
                  <td>{season.episodes?.length || 0} Episoden</td>
                  <td>
                    {season.trailerUrl ? (
                      <a href={season.trailerUrl} target="_blank" rel="noopener noreferrer">
                        <FaPlayCircle /> Trailer
                      </a>
                    ) : (
                      'Kein Trailer'
                    )}
                  </td>
                  <td>
                    <button
                      className="btn btn-sm btn-outline-info me-2"
                      onClick={() => onViewEpisodes(season)}
                    >
                      Episoden
                    </button>
                    <button
                      className="btn btn-sm btn-outline-primary me-2"
                      onClick={() => onEditSeason(season)}
                    >
                      <FaEdit />
                    </button>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => onDeleteSeason(season.seasonNumber)}
                    >
                      <FaTrash />
                    </button>
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

export default SeasonsManagement;