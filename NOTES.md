# Patch Notes

## Summary of changes

- Grouped title/description search conditions so archived tasks and status filters are always respected.
- Added `id` as a deterministic secondary sort key in the H2 and Oracle query definitions.
- Validated `page`, bounded `pageSize` to 100, and return `400 Bad Request` for invalid statuses or pagination values.
- Debounced frontend requests, cancelled superseded requests, and corrected loading/error state cleanup.
- Reset pagination when the search text or status filter changes.

## Not changed

The repository still loads all matching rows before slicing them in Java. Moving pagination into the database would be the better scalable design, but it requires a broader repository/API change than this focused exercise. I also left `LIKE` wildcard escaping, indexes, and the intentional controller delay unchanged.

## Biggest remaining risk

Unbounded in-memory result loading remains the main performance risk as the task table grows. A large or broad search can still consume unnecessary memory and database/CPU time.

## Tools used

Placeholder: used GitHub Copilot for repository review, patch drafting, and build verification; manually reviewed and validated the resulting changes.
