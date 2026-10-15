# Guelmim Express: Sahara Visual Identity & Smooth Uber Eats-Style Navigation

Modernize Guelmim Express with an authentic Sahara color palette featuring warm ochre, deep golden sand, and soft twilight desert-sky blue, while elevating the live tracking into an ultra-smooth Uber Eats-style navigation experience with micro-mobility E-Scooters.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed during Phase 1 interactive clarification:
> - **Visual Palette Accent**: Soft twilight desert-sky azure (`#3B82F6` / `#5C93D6`) paired with warm Sahara ochre (`#C85A32`) and deep golden sand (`#E5A93C`).
> - **E-Scooter Mode**: Dedicated micro-mobility profile cruising at 25 km/h through pedestrian plazas (Place Bir Anzarane), calm medina alleys, and oasis walkways.
> - **Navigation Polish**: Uber Eats-style smooth vehicle glide animation with shortest-angle heading rotation and an expandable dynamic bottom sheet.
> - **Critical Fix**: Restore `signingConfig` assignment in `app/build.gradle.kts` and fix map text rendering to ensure 100% stable execution.

---

### 1. Overview & Core Concept

- **What It Does**: Transforms Guelmim Express into a fluid, visually striking delivery tracking experience. Users track their deliveries with live animated vehicle glides on a custom Guelmim map, switch between 4 vehicle modes (including the new E-Scooter), and experience dynamic bottom sheets with real-time ETAs and turn-by-turn guidance.
- **Target Audience**: Residents, local businesses (Restaurant Bab Sahra, Pâtisserie Tighmert, Marché Amhirich), and couriers navigating the city of Guelmim and the Tighmert Oasis.
- **Key Value**: Real-time delivery transparency, authentic Sahara cultural identity, and effortless multi-vehicle delivery management.

---

### 2. User Experience & Visual Design

#### Visual Identity & Theme Tokens
- **Warm Sahara Ochre** (`#C85A32` / `#D46A38`): Primary brand color for hero buttons, order status milestones, and active routes.
- **Deep Golden Sand** (`#E5A93C` / `#D9822B`): Secondary accent for ratings, speed tags, and dune landscape highlights.
- **Desert-Sky Azure Blue** (`#3B82F6` / `#5C93D6`): Tertiary accent representing the clear Sahara sky, used for live GPS beacons, E-Scooter mode branding, and destination indicators.
- **Dune Sand Surface** (`#FBF7F0` Light, `#1A1513` Dark): Natural parchment/sand texture background avoiding cold grays.

#### Key User Flows & Micro-Interactions
1. **Live Uber Eats-Style Map & Sheet Tracking**:
   - The map takes center stage with subtle dune lines, Oued Noun riverbed contours, and landmark badges (Bab Sahra, Bir Anzarane, Souk Amhirich).
   - The courier vehicle icon glides smoothly along the route with fluid angular rotation and an animated pulsing radar ring.
   - A modern floating bottom sheet transitions smoothly between a compact progress pill and full order receipts with animated chevron toggles.
2. **E-Scooter Navigation Selection**:
   - 4-way vehicle switcher: **Express Moto** (42 km/h), **City Car** (32 km/h), **Eco Bicycle** (18 km/h), and the new **E-Scooter** (25 km/h).
   - Selecting E-Scooter activates dedicated micro-mobility routes through Place Bir Anzarane pedestrian plazas and souk shortcuts with an eco score of 4/5 and 8 DH fare.
3. **Dynamic Multi-Tab App Experience**:
   - **Live Tracking**: Interactive canvas map with zoom/pan, vehicle switcher, ETA countdown, and turn-by-turn prompt.
   - **Shops & Merchants**: Filter local Guelmim vendors with live category pills and 1-tap dispatch.
   - **Courier Dispatch**: Interactive parcel sender across any two Guelmim quarters with live distance & fare recalculation.
   - **Business Portal**: Live orders queue with 1-click status transitions and driver assignment modal.
   - **Driver Cockpit**: Full-screen HUD with digital speedometer and next turn direction.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Native Canvas Interpolation vs. Frame Snapping**
  - *Chosen Approach*: Implement smooth linear/cubic interpolation for vehicle position and shortest-arc angular slerp for rotation in `GuelmimCanvasMap.kt`.
  - *Why*: Eliminates jitter and mimics Uber Eats / Google Maps navigation fluidity.
- **Decision 2: Expandable Floating Sheet vs. Fullscreen Navigation**
  - *Chosen Approach*: A responsive floating card / bottom sheet that retains continuous map visibility while letting users pull up order details or driver chat.
  - *Why*: Maximizes spatial awareness of Guelmim geography while keeping controls at thumb's reach.
- **Decision 3: Dedicated E-Scooter Micro-Mobility Profile**
  - *Chosen Approach*: Model realistic 25 km/h urban speed limits, high agility through congested medina streets, and electric azure branding.
  - *Why*: Reflects modern sustainable urban delivery trends in Moroccan cities.

---

### 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Guelmim Express Mobile App                      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
               ┌────────────────────┼────────────────────┐
               ▼                    ▼                    ▼
     ┌───────────────────┐┌───────────────────┐┌───────────────────┐
     │  Sahara Theme &   ││ DeliveryViewModel ││  GuelmimCanvas   │
     │  Color Tokens     ││   State & Ticker  ││    Map Engine    │
     │ (Ochre, Sand,     ││ (Orders, Drivers, ││(Terrain, Roads,  │
     │  Desert-Sky Blue) ││  Reviews, Telemetry)│Smooth Glide HUD)│
     └───────────────────┘└─────────┬─────────┘└───────────────────┘
                                    │
         ┌──────────────────────────┴──────────────────────────┐
         ▼                          ▼                          ▼
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│  TrackingScreen  │       │  BusinessPortal  │       │ Driver Cockpit & │
│ (Floating Sheet, │       │  (Live Orders,   │       │ Customer Reviews │
│ Turn Guidance)   │       │ Driver Dispatch) │       │ (1-5 Star System)│
└──────────────────┘       └──────────────────┘       └──────────────────┘
```

#### Component & State Responsibilities
- `Color.kt` & `Theme.kt`: Establish Ochre `#C85A32`, Sahara Sand `#E5A93C`, Desert Sky `#3B82F6`, and background tokens.
- `DeliveryModels.kt`: Add `ESCOOTER` to `VehicleType` with distinct parameters, icon, and tag line.
- `GuelmimMapData.kt`: Add E-Scooter waypoint generator utilizing pedestrian plazas and alley shortcuts.
- `GuelmimCanvasMap.kt`: Upgrade vehicle drawing with animated position/heading lerp, fix text rendering with `translate`, and polish route polyline.
- `TrackingScreen.kt`: Upgrade bottom drawer with animated height expansion, delivery timeline progress, and driver rating prompt.
