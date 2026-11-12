export default function StudentTable({ students, loading, searching, onEdit, onDelete }) {
  if (loading && students.length === 0) {
    return <div className="empty">Loading students…</div>;
  }

  if (students.length === 0) {
    return (
      <div className="empty">
        {searching ? "No students match your search." : "No students yet. Click \"+ Add student\" to create one."}
      </div>
    );
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Roll no.</th>
            <th>Email</th>
            <th>Department</th>
            <th>Year</th>
            <th className="actions-col">Actions</th>
          </tr>
        </thead>
        <tbody>
          {students.map((s) => (
            <tr key={s.id}>
              <td className="muted">{s.id}</td>
              <td className="strong">{s.name}</td>
              <td><span className="tag">{s.rollNo}</span></td>
              <td>{s.email}</td>
              <td>{s.department}</td>
              <td>{s.yearOfStudy}</td>
              <td className="actions">
                <button className="btn small" onClick={() => onEdit(s)}>Edit</button>
                <button className="btn small danger" onClick={() => onDelete(s)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
