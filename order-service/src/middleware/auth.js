const jwt = require('jsonwebtoken');

/**
 * Middleware JWT pour vérifier les tokens
 */
const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

    if (!token) {
        return res.status(401).json({
            error: 'Accès non autorisé',
            message: 'Token JWT manquant'
        });
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
        if (err) {
            console.error('JWT verification error:', err.message);
            return res.status(403).json({
                error: 'Token invalide',
                message: 'Token JWT invalide ou expiré'
            });
        }

        // Ajouter les infos utilisateur à la requête
        req.user = decoded;
        next();
    });
};

/**
 * Middleware pour vérifier le rôle admin
 */
const requireAdmin = (req, res, next) => {
    if (!req.user) {
        return res.status(401).json({
            error: 'Non authentifié',
            message: 'Authentification requise'
        });
    }

    if (req.user.role === 'ADMIN') {
        next();
    } else {
        return res.status(403).json({
            error: 'Accès refusé',
            message: 'Rôle administrateur requis'
        });
    }
};

module.exports = {
    authenticateToken,
    requireAdmin
};