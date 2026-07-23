import React from 'react';
import {
  FaAlignLeft,
  FaBriefcase,
  FaEnvelope,
  FaLock,
  FaMoneyBillAlt,
  FaRegIdBadge,
  FaRegUserCircle,
  FaUser,
} from 'react-icons/fa';

type IconProps = { className?: string; 'aria-hidden'?: boolean | 'true' | 'false' };

/**
 * react-icons + @types/react version mismatch can mark icons as invalid JSX.
 * Cast once here so feature components stay clean.
 */
function asIcon(Icon: unknown): React.FC<IconProps> {
  return Icon as React.FC<IconProps>;
}

export const Icons = {
  User: asIcon(FaUser),
  Lock: asIcon(FaLock),
  Envelope: asIcon(FaEnvelope),
  Briefcase: asIcon(FaBriefcase),
  Money: asIcon(FaMoneyBillAlt),
  AlignLeft: asIcon(FaAlignLeft),
  UserCircle: asIcon(FaRegUserCircle),
  IdBadge: asIcon(FaRegIdBadge),
};
