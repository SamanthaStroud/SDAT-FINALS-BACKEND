# Software Study Scripts — Team Submission

**Team members:** Samantha Stroud, Brandon Coish

**Repos:**

- Backend: https://github.com/SamanthaStroud/SDAT-FINALS-BACKEND
- Frontend: https://github.com/SamanthaStroud/SDAT-FINALS-FRONTEND.git

**Demo video:** [Add VIDEO URL or submit separately ]

## Deployment
- **Platform:** AWS (RDS PostgreSQL + EC2 running Docker)
- **Backend live URL:** http://44.210.130.159:8080
- **Database:** Amazon RDS PostgreSQL (db.t3.micro, us-east-1)
- **Compute:** EC2 t2.micro instance running the backend Docker image (pulled from Amazon ECR)
- Verified working: public read endpoints, authenticated write endpoints, login/session auth, and admin-only routes all function correctly against the deployed database.

## Feedback on team performance and my role

Sammie:
I did the User, Auth, Note, Bookmark, and Admin domains on the backend and the unit tests for each (UserServiceTest, NoteServiceTest, AuthControllerTest).
I also set up the session-based Spring Security wiring , a global exception handler so the API returns consistent error responses, and the Dockerfile/docker-compose setup for running the whole stack locally.
Brandon:
I owned the Topic and Concept domain on the backend and full CRUD for both, including the Topic Concept relationship. I modeled the nested lesson content as JSONB columns to keep things manageable within the timeline. I wrote the seed data, unit tests for both service and controller layers, and set up the GitHub Actions CI workflow with a Postgres service container so tests run automatically on every PR. I also built the Topic/Concept CRUD forms into the Admin panel on the frontend, and once Sammie flagged that SecurityConfig needed real beans and route rules, I built that out properly (replacing both our placeholder configs) so her auth code wasn't left on a temporary workaround.

## HONEST REFLECTION HERE —

Sammie:

- Overall we worked well together by splitting the backend into two clear domains (Brandon on Topic/Concept, me on User/Auth/Note/Bookmark/Admin) let us both move independently without stepping on each other's code most of the time.
  The biggest friction point for me was a stretch where `main` was actually broken (missing beans my code depended on) (my bad) until i coordinated on exactly what it needed.
  That was a good lesson: on a shared/foundational file like security config, we should have agreed on the bean contracts up front instead of discovering the gaps after the fact.
  I enjoyed doing this sprint with the Software study scripts frontend and mixing it with the springboot/docker backend.
  Brandon:
  I hit some real, non obvious problems, a port conflict between a native Postgres install and Docker that took a few rounds to diagnose, a reserved SQL keyword breaking table creation, a lazy-loading/serialization bug that only showed up on reads, and a handful of breaking API changes from being on a very new Spring Boot version. Working in parallel with Sammie went smoothly overall since we had clearly separated domains, but the SecurityConfig situation was a real example of a shared dependency not being coordinated ahead of time. Her code needed beans mine didn't define yet, and it briefly broke main. We recovered fine, but it's a good lesson for next time: agree on the shape of shared/foundational files (like security config) before either of us builds on top of them.
