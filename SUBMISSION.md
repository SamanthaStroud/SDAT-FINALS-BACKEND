# Software Study Scripts — Team Submission

**Team members:** Samantha Stroud, Brandon Coish

**Repos:**
- Backend: https://github.com/SamanthaStroud/SDAT-FINALS-BACKEND
- Frontend: https://github.com/SamanthaStroud/SDAT-FINALS-FRONTEND.git

**Demo video:** [Add VIDEO URL or submit separately  ]

## Feedback on team performance and my role
Sammie: 
I did the User, Auth, Note, Bookmark, and Admin domains on the backend and the unit tests for each (UserServiceTest, NoteServiceTest, AuthControllerTest). 
I also set up the session-based Spring Security wiring , a global exception handler so the API returns consistent error responses, and the Dockerfile/docker-compose setup for running the whole stack locally.
Brandon:


## HONEST REFLECTION HERE — 
Sammie:
- Overall we worked well together by splitting the backend into two clear domains (Brandon on Topic/Concept, me on User/Auth/Note/Bookmark/Admin) let us both move independently without stepping on each other's code most of the time.
The biggest friction point for me was a stretch where `main` was actually broken (missing beans my code depended on) (my bad) until i coordinated on exactly what it needed.
That was a good lesson: on a shared/foundational file like security config, we should have agreed on the bean contracts up front instead of discovering the gaps after the fact.
I enjoyed doing this sprint with the Software study scripts frontend and mixing it with the springboot/docker backend. 
Brandon:
- 
