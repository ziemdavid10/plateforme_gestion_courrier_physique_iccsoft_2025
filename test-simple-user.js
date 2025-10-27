const axios = require('axios');

async function testSimple() {
    try {
        console.log('Test simple du service utilisateur...');
        
        // Test 1: Récupérer tous les utilisateurs
        const allUsers = await axios.get('http://localhost:8083/v2/user/all');
        console.log('✅ Tous les utilisateurs:', allUsers.data.length);
        
        if (allUsers.data.length > 0) {
            const firstUser = allUsers.data[0];
            console.log('Premier utilisateur:', firstUser.username);
            
            // Test 2: Récupérer par username
            try {
                const userByUsername = await axios.get(`http://localhost:8083/v2/user/username/${firstUser.username}`);
                console.log('✅ Utilisateur trouvé par username:', userByUsername.data.username);
            } catch (error) {
                console.log('❌ Erreur username endpoint:', error.response?.status, error.response?.data);
            }
        }
        
    } catch (error) {
        console.error('❌ Erreur:', error.message);
    }
}

testSimple();