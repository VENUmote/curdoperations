import { useState } from "react";

const DEPARTMENTS = [
  "Computer Science",
  "Information Technology",
  "Electronics",
  "Electrical",
  "Mechanical",
  "Civil",
];

const EMPTY = { name: "", email: "", rollNo: "", department: "Computer Science", yearOfStudy: 1 };

// Same rules as the backend's @Valid checks, so users get instant feedback.
// (The backend still validates everything — never trust the browser alone.)
function validate(v) {
  const e = {};
  if (v.name.trim().length < 2) e.name = "Name must be at least 2 characters";
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.email.trim())) e.email = "Enter a valid email";
  if (!/^[A-Za-z0-9-]{3,20}$/.test(v.rollNo.trim())) e.rollNo = "3-20 letters, digits or '-'";
  if (!v.department.trim()) e.department = "Department is required";
  if (!(v.yearOfStudy >= 1 && v.yearOfStudy <= 4)) e.yearOfStudy = "Year must be 1-4";
  return e;
}

export default function StudentForm({ initial, onSave, onCancel }) {
  const isEdit = Boolean(initial);
  const [values, setValues] = useState(
    initial
      ? {
          name: initial.name,
          email: initial.email,
          rollNo: initial.rollNo,
          department: initial.department,
          yearOfStudy: initial.yearOfStudy,
        }
      : EMPTY
  );
  const [errors, setErrors] = useState({});
  const [formError, setFormError] = useState("");
  const [saving, setSaving] = useState(false);

  function set(field, value) {
    setValues((v) => ({ ...v, [field]: value }));
    setErrors((e) => ({ ...e, [field]: undefined }));
  }

  async function submit(event) {
    event.preventDefault();
    const clientErrors = validate(values);
    setErrors(clientErrors);
    setFormError("");
    if (Object.keys(clientErrors).length > 0) return;

    setSaving(true);
    try {
      await onSave({ ...values, yearOfStudy: Number(values.yearOfStudy) });
    } catch (err) {
      // Show backend messages, e.g. "Roll number 'CS2041' already exists"
      setErrors(err.fieldErrors || {});
      setFormError(err.message);
      setSaving(false);
    }
  }

  const deptOptions = DEPARTMENTS.includes(values.department) ? DEPARTMENTS : [values.department, ...DEPARTMENTS];

  return (
    <div className="overlay" onMouseDown={(e) => e.target === e.currentTarget && onCancel()}>
      <form className="dialog" onSubmit={submit} noValidate>
        <h2>{isEdit ? "Edit student" : "Add student"}</h2>

        {formError && <div className="alert">{formError}</div>}

        <Field label="Full name" error={errors.name}>
          <input value={values.name} onChange={(e) => set("name", e.target.value)} autoFocus />
        </Field>

        <Field label="Email" error={errors.email}>
          <input type="email" value={values.email} onChange={(e) => set("email", e.target.value)} />
        </Field>

        <div className="row">
          <Field label="Roll number" error={errors.rollNo}>
            <input value={values.rollNo} onChange={(e) => set("rollNo", e.target.value)} placeholder="e.g. CS2041" />
          </Field>
          <Field label="Year" error={errors.yearOfStudy}>
            <select value={values.yearOfStudy} onChange={(e) => set("yearOfStudy", Number(e.target.value))}>
              {[1, 2, 3, 4].map((y) => <option key={y} value={y}>{y}</option>)}
            </select>
          </Field>
        </div>

        <Field label="Department" error={errors.department}>
          <select value={values.department} onChange={(e) => set("department", e.target.value)}>
            {deptOptions.map((d) => <option key={d}>{d}</option>)}
          </select>
        </Field>

        <div className="dialog-actions">
          <button type="button" className="btn" onClick={onCancel}>Cancel</button>
          <button type="submit" className="btn primary" disabled={saving}>
            {saving ? "Saving…" : isEdit ? "Save changes" : "Add student"}
          </button>
        </div>
      </form>
    </div>
  );
}

function Field({ label, error, children }) {
  return (
    <label className={`field ${error ? "has-error" : ""}`}>
      <span>{label}</span>
      {children}
      {error && <small>{error}</small>}
    </label>
  );
}
