# ShadowTaxi

## Overview
"ShadowTaxi" is a survival-based game where you navigate the challenges of being a taxi driver during an economic crisis. Manage resources, avoid hazards, and deliver passengers safely to achieve a target score before time runs out.

![image](https://github.com/user-attachments/assets/dad06346-aca2-4512-9c03-bfca0346c10c)

---

## Gameplay Features
1. **Objective**:  
   - Earn a target score of **500** by completing passenger trips and collecting coins within **15,000 frames**.  

2. **Core Mechanics**:  
   - **Taxi Movement**: Use arrow keys to move left, right, or up.  
   - **Passenger Pickup & Drop-off**: Stop near passengers to pick them up and deliver them to their designated trip end flags. Stopping beyond the flag incurs a penalty.  
   - **Collect Coins**: Increase passenger priority (and trip earnings) by collecting coins.  

3. **Hazards**:  
   - Enemy cars and other vehicles will damage the taxi, driver, and passengers on collision.  

4. **Health System**:  
   - The driver, passengers, and taxi have individual health values. If any health drops below 0, the game ends in a loss.  

5. **Taxi Replacement**:  
   - If a taxi is damaged beyond repair, a new taxi is spawned. The driver must move to the new taxi before it leaves the screen, or it results in a loss.  

---

## Winning Conditions
- Achieve a total score of **500** before the timer runs out.  
- Avoid health depletion for the driver, passengers, or taxi.  

## Losing Conditions
- Health of driver or passenger falls below 0.  
- The game exceeds **15,000 frames** without meeting the target score.  
- A new taxi leaves the screen without the driver boarding it.  

---

## Advanced Features
- **Priority System**: Collect coins to boost passenger priority and increase trip earnings.  
- **Dynamic Enemy Behavior**: Enemy cars and other vehicles move randomly, adding unpredictability.  
- **Leaderboard**: Displays the top 5 scores at the end of each game.  

---

## Controls
- **Arrow Keys**: Move the taxi or driver.  
- **Stop Near Entities**: Interact with passengers, flags, or coins by stopping close to them.  

---

## Tools used  
- Java
- BAGEL (Java Game Engine Package)
  
---

## Contributors
- Kerui Huang
- University of Melbourne SWEN20003 Teaching Team (Skeleton Code and Game Concept Provider)
  
---

## License
This project is for academic purposes under the University of Melbourne's SWEN20003 course.



