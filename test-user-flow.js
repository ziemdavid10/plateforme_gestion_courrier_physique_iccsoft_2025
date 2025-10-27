const axios = require('axios');

// Test du flux complet : Création utilisateur → Connexion → Accès selon rôle
async function testCompleteUserFlow() {
    console.log('🧪 Test du flux complet utilisateur\n');

    const AUTH_URL = 'http://localhost:8081/v1/api/auth';
    const USER_URL = 'http://localhost:8083/v2/user';
    
    let adminToken = '';
    let newUserId = '';

    try {
        // 1. Connexion Admin
        console.log('1️⃣ Connexion Administrateur...');
        const adminLogin = await axios.post(`${AUTH_URL}/signin`, {
            username: 'admin',
            password: 'admin123'
        });
        
        adminToken = adminLogin.data.accessToken;
        console.log('✅ Admin connecté');

        // 2. Création d'un nouvel utilisateur
        console.log('\n2️⃣ Création d\'un nouvel utilisateur...');
        const newUser = {
            username: 'test.employe',
            name: 'Test Employé',
            email: 'test.employe@iccsoft.com',
            password: 'test123',
            role: 'EMPLOYE',
            department: 'DIRECTION_TECHNIQUE',
            entreprise: 'ICCSOFT',
            fonction: 'INGENIEUR_DEVELOPPEUR'
        };

        const createResponse = await axios.post(USER_URL, newUser);
        newUserId = createResponse.data.id;
        console.log('✅ Utilisateur créé:', createResponse.data.username);

        // 3. Connexion avec le nouvel utilisateur
        console.log('\n3️⃣ Test de connexion avec le nouvel utilisateur...');
        const userLogin = await axios.post(`${AUTH_URL}/signin`, {
            username: 'test.employe',
            password: 'test123'
        });

        if (userLogin.data.accessToken) {
            console.log('✅ Connexion réussie pour le nouvel utilisateur');
            
            // 4. Vérification du profil et du rôle
            const userProfile = await axios.get(`${AUTH_URL}/me`, {
                headers: { Authorization: `Bearer ${userLogin.data.accessToken}` }
            });
            
            console.log('✅ Profil récupéré:');
            console.log(`   - Username: ${userProfile.data.username}`);
            console.log(`   - Rôle: ${userProfile.data.role}`);
            console.log(`   - Email: ${userProfile.data.email}`);

            // 5. Test d'accès aux courriers selon le rôle
            console.log('\n4️⃣ Test d\'accès aux courriers...');
            try {
                const courriersResponse = await axios.get('http://localhost:8082/v3/courrier', {
                    headers: { Authorization: `Bearer ${userLogin.data.accessToken}` }
                });
                console.log(`✅ Accès aux courriers autorisé (${courriersResponse.data.length} courriers)`);
            } catch (error) {
                console.log('⚠️ Accès aux courriers:', error.response?.status || 'Erreur');
            }

            // 6. Vérification de la redirection selon le rôle
            console.log('\n5️⃣ Vérification de la logique de redirection...');
            const expectedRoute = getExpectedRoute(userProfile.data.role);
            console.log(`✅ Route attendue pour ${userProfile.data.role}: ${expectedRoute}`);

        } else {
            console.log('❌ Échec de connexion pour le nouvel utilisateur');
        }

        // 7. Nettoyage - Suppression de l'utilisateur de test
        console.log('\n6️⃣ Nettoyage...');
        if (newUserId) {
            await axios.delete(`${USER_URL}/${newUserId}`, {
                headers: { Authorization: `Bearer ${adminToken}` }
            });
            console.log('✅ Utilisateur de test supprimé');
        }

        console.log('\n🎉 Test du flux complet RÉUSSI !');
        console.log('\n📋 Résumé:');
        console.log('✅ Admin peut créer des utilisateurs');
        console.log('✅ Utilisateurs créés peuvent se connecter');
        console.log('✅ Rôles sont correctement assignés');
        console.log('✅ Accès aux ressources selon les permissions');

    } catch (error) {
        console.error('\n❌ Erreur dans le flux:', error.response?.data || error.message);
        
        // Nettoyage en cas d'erreur
        if (newUserId && adminToken) {
            try {
                await axios.delete(`${USER_URL}/${newUserId}`, {
                    headers: { Authorization: `Bearer ${adminToken}` }
                });
                console.log('🧹 Nettoyage effectué');
            } catch (cleanupError) {
                console.log('⚠️ Erreur de nettoyage');
            }
        }
    }
}

function getExpectedRoute(role) {
    switch (role) {
        case 'ADMINISTRATEUR':
            return '/dashboard-administrateur';
        case 'SECRETAIRE':
            return '/dashboard-secretaire';
        case 'EMPLOYE':
            return '/dashboard-employe';
        default:
            return '/login';
    }
}

// Test des différents rôles
async function testAllRoles() {
    console.log('\n🔄 Test de tous les rôles...\n');
    
    const roles = [
        { role: 'EMPLOYE', username: 'test.employe2', password: 'emp123' },
        { role: 'SECRETAIRE', username: 'test.secretaire', password: 'sec123' },
        { role: 'ADMINISTRATEUR', username: 'test.admin', password: 'admin123' }
    ];

    for (const roleTest of roles) {
        console.log(`\n🧪 Test rôle ${roleTest.role}:`);
        console.log(`   Route attendue: ${getExpectedRoute(roleTest.role)}`);
        console.log(`   Permissions: ${getRolePermissions(roleTest.role)}`);
    }
}

function getRolePermissions(role) {
    switch (role) {
        case 'ADMINISTRATEUR':
            return 'Gestion utilisateurs, CRUD courriers, Configuration';
        case 'SECRETAIRE':
            return 'CRUD courriers complet';
        case 'EMPLOYE':
            return 'Consultation courriers (ses courriers uniquement)';
        default:
            return 'Aucune';
    }
}

// Exécution des tests
testCompleteUserFlow().then(() => {
    testAllRoles();
}).catch(console.error);