# Project Requirements

## Summary
The goal of this project is to create a Java Full Stack Employee Reimbursement System (ERS) by Madasu Rakesh. The main use case of the ERS centers around Employees submitting Reimbursements that can either be accepted or denied by Managers. The tech stack will consist of a React-Based Front end, communicating via HTTP to a Spring-Based Back end. The database will be either a local or cloud-based Postgres database.

## User Stories

### Employee User Stories
- Create an account (create new User – default role should be employee)
- Create a new Reimbursement
- See all reimbursement tickets (only their own)
- See only their pending reimbursement tickets
- [Some other functionality of your choice]
- OPTIONAL: Update the description of a pending reimbursement

### Manager User Stories
- See all reimbursements
- See all pending reimbursements
- Resolve a reimbursement (update status from PENDING to APPROVED or DENIED)
- See all Users
- Delete a User (should also delete any related reimbursements)
- OPTIONAL: Update an employee’s status to manager

### Validation User Stories (Do Login Last!!)
- Attempt to log in.
- Create an account
- Users should not be able to access the other user stories before logging in.

### Optional User Stories (Only try these after completing the stories above)
- Logging of the Service layer with logback.
- Test Suites for the Service layer with JUnit

## Database Architecture
- Change table columns as you see fit, but keep in mind that these are the diagram shows the absolute minimum requirements.
- Add constraints to the tables as you see fit, and make sure to error handle for them!
