import { useCallback, useEffect, useState } from "react";
import { createStudent, deleteStudent, getStudents, updateStudent } from "./api.js";
import StudentForm from "./components/StudentForm.jsx";
import StudentTable from "./components/StudentTable.jsx";
import ConfirmDialog from "./components/ConfirmDialog.jsx";

export default function App() {
  const [students, setStudents] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState("");
  const [editing, setEditing] = useState(null);      // student being edited, or null = "add" mode
  const [formOpen, setFormOpen] = useState(false);
  const [toDelete, setToDelete] = useState(null);
  const [toast, setToast] = useState("");

  // READ: load (or reload) the list whenever the search text changes
  const load = useCallback(async (text) => {
    setLoading(true);
    setLoadError("");
    try {
      setStudents(await getStudents(text));
    } catch (err) {
      setLoadError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => load(search), 250); // small delay while typing
    return () => clearTimeout(timer);
  }, [search, load]);

  function showToast(message) {
    setToast(message);
    setTimeout(() => setToast(""), 2500);
  }

  function openAdd() {
    setEditing(null);
    setFormOpen(true);
  }

  function openEdit(student) {
    setEditing(student);
    setFormOpen(true);
  }

  // CREATE or UPDATE (errors are thrown back to the form so it can show field messages)
  async function handleSave(data) {
    if (editing) {
      await updateStudent(editing.id, data);
      showToast(`Updated ${data.name}`);
    } else {
      await createStudent(data);
      showToast(`Added ${data.name}`);
    }
    setFormOpen(false);
    setEditing(null);
    load(search);
  }

  // DELETE
  async function handleDelete() {
    const student = toDelete;
    setToDelete(null);
    try {
      await deleteStudent(student.id);
      showToast(`Deleted ${student.name}`);
      load(search);
    } catch (err) {
      showToast(err.message);
    }
  }

  return (
    <div className="page">
      <header className="header">
        <div>
          <p className="eyebrow">Java Full Stack · Spring Boot + React</p>
          <h1>Student Management System</h1>
        </div>
        <button className="btn primary" onClick={openAdd}>+ Add student</button>
      </header>

      <div className="toolbar">
        <input
          className="search"
          type="search"
          placeholder="Search by name, roll no., email or department"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <span className="count">{students.length} student{students.length === 1 ? "" : "s"}</span>
      </div>

      {loadError ? (
        <div className="alert">
          <strong>Could not load students.</strong> {loadError}
          <button className="btn small" onClick={() => load(search)}>Retry</button>
        </div>
      ) : (
        <StudentTable
          students={students}
          loading={loading}
          searching={search.trim().length > 0}
          onEdit={openEdit}
          onDelete={setToDelete}
        />
      )}

      {formOpen && (
        <StudentForm
          initial={editing}
          onSave={handleSave}
          onCancel={() => { setFormOpen(false); setEditing(null); }}
        />
      )}

      {toDelete && (
        <ConfirmDialog
          title="Delete student?"
          message={`${toDelete.name} (${toDelete.rollNo}) will be removed permanently.`}
          confirmLabel="Delete"
          onConfirm={handleDelete}
          onCancel={() => setToDelete(null)}
        />
      )}

      {toast && <div className="toast" role="status">{toast}</div>}
    </div>
  );
}
