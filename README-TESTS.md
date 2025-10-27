# 🧪 Guide de Test d'Intégration Frontend-Backend

## 📋 Prérequis

### Services Backend à démarrer :
1. **Service d'Authentification** (Port 8081)
   ```bash
   cd back_end_courrier/demo
   mvn spring-boot:run
   ```

2. **Service Courrier** (Port 8082)
   ```bash
   cd back_end_courrier/iccsoft_courrier
   mvn spring-boot:run
   ```

3. **Service Utilisateur** (Port 8083)
   ```bash
   cd back_end_courrier/iccsoft_user
   mvn spring-boot:run
   ```

4. **Frontend Angular** (Port 4200)
   ```bash
   cd front_end_courrier
   ng serve
   ```

## 🔧 Méthodes de Test

### 1. Test de Connectivité Rapide
```bash
node test-simple.js
```
Vérifie si tous les services répondent.

### 2. Test Complet via Interface Web
1. Ouvrir `test-frontend-backend.html` dans le navigateur
2. Cliquer sur "Exécuter Tous les Tests"
3. Consulter les logs en temps réel

### 3. Test Programmatique Complet
```bash
node test-integration.js
```
Exécute tous les tests CRUD automatiquement.

## 📊 Tests Inclus

### ✅ Test d'Authentification
- Connexion utilisateur
- Récupération du profil
- Validation du token JWT

### ✅ Test CRUD Courriers
- Création de courrier
- Lecture des courriers
- Modification de courrier
- Suppression de courrier

### ✅ Test Gestion Utilisateurs
- Récupération des utilisateurs
- Création d'utilisateur
- Suppression d'utilisateur

### ✅ Test de Sécurité
- Vérification des rôles
- Contrôle d'accès aux endpoints
- Validation des headers

## 🚨 Résolution des Problèmes

### Services non accessibles
- Vérifier que tous les services sont démarrés
- Contrôler les ports (8081, 8082, 8083, 4200)
- Vérifier les logs des services

### Erreurs CORS
- Les configurations CORS ont été mises à jour
- Redémarrer les services après modifications

### Erreurs d'authentification
- Un utilisateur de test sera créé automatiquement
- Credentials par défaut : admin/admin123

## 📈 Résultats Attendus

### ✅ Succès Complet
Tous les services répondent et les opérations CRUD fonctionnent.

### ⚠️ Succès Partiel
Certains services répondent mais avec des erreurs fonctionnelles.

### ❌ Échec
Services non démarrés ou erreurs de configuration.

## 🔍 Endpoints Testés

| Service | Endpoint | Méthode | Description |
|---------|----------|---------|-------------|
| Auth | `/v1/api/auth/signin` | POST | Connexion |
| Auth | `/v1/api/auth/me` | GET | Profil utilisateur |
| Courrier | `/v3/courrier` | GET | Liste courriers |
| Courrier | `/v3/courrier` | POST | Créer courrier |
| Courrier | `/v3/courrier/{id}` | PUT | Modifier courrier |
| Courrier | `/v3/courrier/{id}` | DELETE | Supprimer courrier |
| User | `/v2/user/all` | GET | Liste utilisateurs |
| User | `/v2/user` | POST | Créer utilisateur |

## 🎯 Validation de l'Intégration

L'intégration est considérée comme **réussie** si :
- ✅ Tous les services sont accessibles
- ✅ L'authentification fonctionne
- ✅ Les opérations CRUD sont opérationnelles
- ✅ Les contrôles de sécurité sont effectifs
- ✅ Les données sont cohérentes entre frontend et backend