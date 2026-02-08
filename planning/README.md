Planning Markdown System

Purpose
- Lightweight planning in Markdown instead of Jira.
- Keep work visible in the repo and close to the code.

Files
- planning/roadmap.md: epics, milestones, target dates.
- planning/backlog.md: prioritized tasks and stories.
- planning/now.md: single active task.
- planning/done.md: completed work log.
- planning/decisions.md: ADR-lite decisions log.
- planning/risks.md: risks, blockers, mitigation.
- docs/firestore-schema.md: Firestore collections reference.

Workflow
- Add new work to planning/backlog.md first.
- Move the top item into planning/now.md (only one active task).
- Update statuses daily (todo, in-progress, blocked, done).
- When done, move the task to planning/done.md with a short result.
- Record significant decisions in planning/decisions.md.
- Track active risks in planning/risks.md.

Task Fields
- ID: unique short id (V2-001).
- Title: short and clear.
- Priority: P0, P1, P2.
- Status: todo, in-progress, blocked, done.
- Acceptance: testable outcomes.
- Dependencies: IDs or links.
- Notes: links to code, designs, docs.
